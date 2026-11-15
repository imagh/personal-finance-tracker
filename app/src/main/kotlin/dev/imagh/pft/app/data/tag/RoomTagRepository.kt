package dev.imagh.pft.app.data.tag

import dev.imagh.pft.app.data.common.RoomEntityRepository
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.Tag
import dev.imagh.pft.ledger.tag.TagRepository

/** The Room implementation of core's [TagRepository]: the shared base plus the tag query. */
class RoomTagRepository(
    private val dao: TagDao,
) : RoomEntityRepository<Tag, TagEntity, TagId>(dao, "tag", TagMapper),
    TagRepository {
    override suspend fun allLive(): List<Tag> = dao.allLive().map(TagMapper::toModel)
}
