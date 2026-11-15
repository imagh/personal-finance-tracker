package dev.imagh.pft.app.data.tag

import dev.imagh.pft.app.data.common.EntityMapper
import dev.imagh.pft.app.data.common.MetaMapper
import dev.imagh.pft.ledger.ids.TagId
import dev.imagh.pft.ledger.tag.Tag

/**
 * Maps a core [Tag] to its [TagEntity] row and back. A tag is only a name, so there is no stored
 * value to validate on read: the name is shown as text and never interpreted.
 */
object TagMapper : EntityMapper<Tag, TagEntity> {
    override fun toRow(model: Tag): TagEntity = TagEntity(meta = MetaMapper.toColumns(model.meta), name = model.name)

    override fun toModel(row: TagEntity): Tag = Tag(meta = MetaMapper.toMeta(row.meta, ::TagId), name = row.name)
}
