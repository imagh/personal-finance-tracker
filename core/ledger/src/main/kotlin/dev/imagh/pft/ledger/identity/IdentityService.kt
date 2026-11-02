package dev.imagh.pft.ledger.identity

import dev.imagh.pft.ledger.audit.AuditAction
import dev.imagh.pft.ledger.audit.AuditDiff
import dev.imagh.pft.ledger.audit.AuditRecorder
import dev.imagh.pft.ledger.entity.Auditable
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId
import dev.imagh.pft.ledger.ids.Uuid7
import dev.imagh.pft.ledger.settings.AppSetting
import dev.imagh.pft.ledger.settings.AppSettingsRepository
import dev.imagh.pft.ledger.settings.SettingKeys
import dev.imagh.pft.ledger.tx.TransactionRunner
import dev.imagh.pft.ledger.validation.LedgerError
import dev.imagh.pft.ledger.validation.LedgerLimits
import dev.imagh.pft.ledger.validation.Validation
import java.time.Clock

/** Who is using this device: the household member and the installation. Attributes every write. */
data class Identity(
    val user: UserProfile,
    val device: Device,
)

/**
 * First-run setup and "who am I" (L-8). The current device is remembered in the
 * `current_device_id` app setting; the user and device come from [IdentityRepository].
 *
 * Setup writes its records and their audit entries in one transaction, attributed to the identity
 * it creates (there is no earlier identity to attribute them to).
 */
class IdentityService(
    private val runner: TransactionRunner,
    private val identities: IdentityRepository,
    private val settings: AppSettingsRepository,
    private val recorder: AuditRecorder,
    private val ids: Uuid7,
    private val clock: Clock,
) {
    /** The household members the first-run screen can offer to pick from. */
    suspend fun users(): List<UserProfile> = identities.users()

    /**
     * This installation's identity, or null before first-run setup.
     *
     * @throws LedgerError.IdentityCorrupt if the current-device setting exists but its device or
     *   user does not. Failing closed stops the app from treating a damaged database as new.
     */
    suspend fun currentIdentity(): Identity? {
        val deviceId = settings.get(SettingKeys.CURRENT_DEVICE_ID)?.value ?: return null
        val device =
            deviceId.takeIf { it.isNotBlank() }?.let { identities.findDevice(DeviceId(it)) }
                ?: throw LedgerError.IdentityCorrupt()
        val user = identities.findUser(device.userId) ?: throw LedgerError.IdentityCorrupt()
        return Identity(user, device)
    }

    /**
     * The identity to attribute a write to.
     *
     * @throws LedgerError.NotInitialised before first-run setup.
     * @throws LedgerError.IdentityCorrupt see [currentIdentity].
     */
    suspend fun requireIdentity(): Identity = currentIdentity() ?: throw LedgerError.NotInitialised()

    /**
     * First run: creates a new user named [userName] and a device labelled [deviceLabel], makes
     * the device current, and audits all three records (CREATE) in one transaction.
     *
     * @throws LedgerError.BlankName / [LedgerError.NameTooLong] for an invalid name or label.
     * @throws LedgerError.AlreadyInitialised if this device already has an identity.
     */
    suspend fun initialise(
        userName: String,
        deviceLabel: String,
    ): Identity {
        val name = Validation.requireName(userName, LedgerLimits.MAX_NAME_LENGTH)
        val label = Validation.requireName(deviceLabel, LedgerLimits.MAX_NAME_LENGTH)
        return runner.inTransaction {
            requireUninitialised()
            val user = UserProfile(UserId(ids.next()), name)
            identities.insertUser(user)
            register(user, label, createdUser = user)
        }
    }

    /**
     * First run on a device where the household member [userId] already exists (for example
     * restored data): creates only the device and the current-device setting.
     *
     * @throws LedgerError.NotFound if no such user exists.
     * @throws LedgerError.AlreadyInitialised if this device already has an identity.
     */
    suspend fun initialiseForExistingUser(
        userId: UserId,
        deviceLabel: String,
    ): Identity {
        val label = Validation.requireName(deviceLabel, LedgerLimits.MAX_NAME_LENGTH)
        return runner.inTransaction {
            requireUninitialised()
            val user = identities.findUser(userId) ?: throw LedgerError.NotFound("user")
            register(user, label, createdUser = null)
        }
    }

    private suspend fun requireUninitialised() {
        if (settings.get(SettingKeys.CURRENT_DEVICE_ID) != null) throw LedgerError.AlreadyInitialised()
    }

    /** Creates the device and the current-device setting, and audits everything created. */
    private suspend fun register(
        user: UserProfile,
        label: String,
        createdUser: UserProfile?,
    ): Identity {
        val device = Device(DeviceId(ids.next()), user.id, label)
        identities.insertDevice(device)
        val setting = AppSetting(SettingKeys.CURRENT_DEVICE_ID, device.id.value, clock.instant())
        settings.put(setting)

        val identity = Identity(user, device)
        if (createdUser != null) auditCreate(createdUser, identity)
        auditCreate(device, identity)
        auditCreate(setting, identity)
        return identity
    }

    private suspend fun auditCreate(
        subject: Auditable,
        by: Identity,
    ) = recorder.record(subject, AuditAction.CREATE, AuditDiff.diff(null, subject), by)
}
