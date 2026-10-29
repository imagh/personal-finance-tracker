package dev.imagh.pft.ledger.identity

import dev.imagh.pft.ledger.entity.Auditable
import dev.imagh.pft.ledger.ids.DeviceId
import dev.imagh.pft.ledger.ids.UserId

/**
 * One installation of the app, owned by a [UserProfile]. Records carry the device they were
 * created on, which later lets sync tell the two phones apart. The installation's own device is
 * named by the `current_device_id` setting.
 *
 * @param label a name the user recognises, such as "Pixel 8".
 */
data class Device(
    val id: DeviceId,
    val userId: UserId,
    val label: String,
) : Auditable {
    override val auditType: String get() = "device"
    override val auditId: String get() = id.value

    override fun auditFields(): Map<String, String?> = mapOf("user_id" to userId.value, "label" to label)
}
