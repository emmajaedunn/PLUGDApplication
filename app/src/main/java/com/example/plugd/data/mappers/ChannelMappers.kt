package com.example.plugd.data.mappers

import com.example.plugd.data.localRoom.entity.ChannelEntity
import com.example.plugd.model.Channel

fun ChannelEntity.toChannel(): Channel = Channel(
    id = channelId,   // <- use channelId
    name = name
)

fun Channel.toChannelEntity(): ChannelEntity = ChannelEntity(
    channelId = id,   // <- map id to channelId
    name = name
)