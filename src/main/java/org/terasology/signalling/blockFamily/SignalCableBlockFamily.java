/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.signalling.blockFamily;

import org.terasology.blockNetwork.BlockNetworkUtil;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.math.Rotation;
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.math.geom.Vector3i;
import org.terasology.signalling.components.SignalConductorComponent;
import org.terasology.signalling.components.SignalConsumerComponent;
import org.terasology.signalling.components.SignalProducerComponent;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockBuilderHelper;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.BlockUri;
import org.terasology.world.block.family.BlockSections;
import org.terasology.world.block.family.MultiConnectFamily;
import org.terasology.world.block.family.RegisterBlockFamily;
import org.terasology.world.block.loader.BlockFamilyDefinition;
import org.terasology.world.block.shapes.BlockShape;

/**
 * SignalCableBlockFamily defines the block family for the cable in the Signalling module.
 * It extends {@link org.terasology.world.block.family.MultiConnectFamily MultiConnectFamily},
 * a very versatile block family for families that require multiple connections, hence the name.
 * 
 * @author Marcin Sciesinski <marcins78@gmail.com>
 */
@RegisterBlockFamily("cable")
@BlockSections({"no_connections", "one_connection", "line_connection", "2d_corner", "3d_corner", "2d_t", "cross", "3d_side", "five_connections", "all"})
public class SignalCableBlockFamily extends MultiConnectFamily {
    /**
     * This constructor isn't used, but it's here just in case
     * 
     * @param definition The block family definition, as passed in by the engine
     * @param shape The shape of the block
     * @param blockBuilder The block builder, as passed in by the engine
     */
    public SignalCableBlockFamily(BlockFamilyDefinition definition, BlockShape shape, BlockBuilderHelper blockBuilder) {
        super(definition, shape, blockBuilder);
    }

    /**
     * This is the constructor that should called by the engine to make this block family.
     * It first calls it's super constructor, then gets it's block URI to base the individual block URIs off of.
     * The next ten lines "register" the blocks, see {@link org.terasology.world.block.family.MultiConnectFamily.registerBlock MultiConnect#registerBlock}
     * 
     * @param definition The block family definition, as passed in by the engine
     * @param blockBuilder The block builder, as passed in by the engine
     */
    public SignalCableBlockFamily(BlockFamilyDefinition definition, BlockBuilderHelper blockBuilder) {
        super(definition, blockBuilder);

        BlockUri blockUri = new BlockUri(definition.getUrn());

        this.registerBlock(blockUri, definition, blockBuilder, "no_connections", (byte) 0, Rotation.allValues());
        this.registerBlock(blockUri, definition, blockBuilder, "one_connection", SideBitFlag.getSides(Side.BACK), Rotation.allValues());
        this.registerBlock(blockUri, definition, blockBuilder, "line_connection", SideBitFlag.getSides(Side.BACK, Side.FRONT), Rotation.allValues());
        this.registerBlock(blockUri, definition, blockBuilder, "2d_corner", SideBitFlag.getSides(Side.LEFT, Side.BACK), Rotation.allValues());
        this.registerBlock(blockUri, definition, blockBuilder, "3d_corner", SideBitFlag.getSides(Side.LEFT, Side.BACK, Side.TOP), Rotation.allValues());
        this.registerBlock(blockUri, definition, blockBuilder, "2d_t", SideBitFlag.getSides(Side.LEFT, Side.BACK, Side.FRONT), Rotation.allValues());
        this.registerBlock(blockUri, definition, blockBuilder, "cross", SideBitFlag.getSides(Side.RIGHT, Side.LEFT, Side.BACK, Side.FRONT), Rotation.allValues());
        this.registerBlock(blockUri, definition, blockBuilder, "3d_side", SideBitFlag.getSides(Side.LEFT, Side.BACK, Side.FRONT, Side.TOP), Rotation.allValues());
        this.registerBlock(blockUri, definition, blockBuilder, "five_connections", SideBitFlag.getSides(Side.LEFT, Side.BACK, Side.FRONT, Side.TOP, Side.BOTTOM), Rotation.allValues());
        this.registerBlock(blockUri, definition, blockBuilder, "all", SideBitFlag.getSides(Side.LEFT, Side.BACK, Side.FRONT, Side.TOP, Side.BOTTOM, Side.RIGHT), Rotation.allValues());
    }

    /**
     * @return All of the sides because cables can connect on any side
     */
    @Override
    public byte getConnectionSides() {
        return SideBitFlag.getSides(Side.LEFT, Side.BACK, Side.FRONT, Side.TOP, Side.BOTTOM, Side.RIGHT);
    }

    //    @Override
    //    public boolean horizontalOnly() {
    //        return false;
    //    }

    /**
     * Sides.LEFT and Sides.RIGHT map to a straight cable
     */
    @Override
    public Block getArchetypeBlock() {
        return blocks.get(SideBitFlag.getSides(Side.RIGHT, Side.LEFT));
    }

    @Override
    public boolean connectionCondition(Vector3i blockLocation, Side connectSide) {
        Vector3i neighborLocation = new Vector3i(blockLocation);
        neighborLocation.add(connectSide.getVector3i());

        byte sourceConnection = BlockNetworkUtil.getSourceConnections(worldProvider.getBlock(blockLocation), SideBitFlag.getSide(connectSide));

        boolean input = false;
        boolean output = false;

        Prefab prefab = this.getArchetypeBlock().getPrefab().get();
        for (SignalConductorComponent.ConnectionGroup connectionGroup : prefab.getComponent(SignalConductorComponent.class).connectionGroups) {
            input |= (connectionGroup.inputSides & sourceConnection) > 0;
            output |= (connectionGroup.outputSides & sourceConnection) > 0;
        }


        if (!input && !output) {
            return false;
        }
        EntityRef neighborEntity = blockEntityRegistry.getBlockEntityAt(neighborLocation);
        return neighborEntity != null && connectsToNeighbor(connectSide, input, output, neighborEntity);
    }


    /**
     * Tests if the block should form a connection on a given side to a given neighbor entity
     * 
     * @param connectSide The side of the original block in question
     * @param input Boolean indicating if the cable has an input line
     * @param output Boolean indicating if the cable block has an output line
     * @param neighborEntity The entity of the neighboring block on connectSide
     * 
     * @return True if the cable should connect to the connectSide
     */
    private boolean connectsToNeighbor(Side connectSide, boolean input, boolean output, EntityRef neighborEntity) {
        final Side oppositeDirection = connectSide.reverse();

        Block block = neighborEntity.getComponent(BlockComponent.class).getBlock();

        final SignalConductorComponent neighborConductorComponent = neighborEntity.getComponent(SignalConductorComponent.class);
        if (neighborConductorComponent != null) {
            if (output) {
                for (SignalConductorComponent.ConnectionGroup connectionGroup : neighborConductorComponent.connectionGroups) {
                    if (SideBitFlag.hasSide(BlockNetworkUtil.getResultConnections(block, connectionGroup.inputSides), oppositeDirection)) {
                        return true;
                    }
                }
            }
            if (input) {
                for (SignalConductorComponent.ConnectionGroup connectionGroup : neighborConductorComponent.connectionGroups) {
                    if (SideBitFlag.hasSide(BlockNetworkUtil.getResultConnections(block, connectionGroup.inputSides), oppositeDirection)) {
                        return true;
                    }
                }
            }
        }

        if (output) {
            final SignalConsumerComponent neighborConsumerComponent = neighborEntity.getComponent(SignalConsumerComponent.class);
            if (neighborConsumerComponent != null && SideBitFlag.hasSide(BlockNetworkUtil.getResultConnections(block, neighborConsumerComponent.connectionSides), oppositeDirection)) {
                return true;
            }
        }
        if (input) {
            final SignalProducerComponent neighborProducerComponent = neighborEntity.getComponent(SignalProducerComponent.class);
            if (neighborProducerComponent != null && SideBitFlag.hasSide(BlockNetworkUtil.getResultConnections(block, neighborProducerComponent.connectionSides), oppositeDirection)) {
                return true;
            }
        }

        return false;
    }


}
