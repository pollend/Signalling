/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.signalling.componentSystem;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.health.BeforeDestroyEvent;
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.math.geom.Vector3i;
import org.terasology.signalling.blockFamily.SignalUpdateFamily;
import org.terasology.signalling.components.CableComponent;
import org.terasology.signalling.components.LeafNodeComponent;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.family.BlockFamily;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@RegisterSystem(RegisterMode.AUTHORITY)
public class NetworkSystem extends BaseComponentSystem {
    @In
    private BlockEntityRegistry blockEntityRegistry;

    public Side getConnections(EntityRef entityRef) {
        BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);
        Block block = blockComponent.getBlock();
        BlockFamily blockFamily =  block.getBlockFamily();
        if(blockFamily instanceof SignalUpdateFamily)
        {
            blockFamily.
        }
    }


    public Map<EntityRef, Byte> findLeafNodesWithSide(Vector3i location) {
        Map<EntityRef, Byte> entries = Maps.newHashMap();
        EntityRef startEntityRef = blockEntityRegistry.getBlockEntityAt(location);
        if (startEntityRef.hasComponent(LeafNodeComponent.class)) {
            LeafNodeComponent leafNodeComponent = startEntityRef.getComponent(LeafNodeComponent.class);
            entries.put(startEntityRef, leafNodeComponent.connections);
            return entries;
        }
        if (startEntityRef.hasComponent(CableComponent.class)) {
            Set<EntityRef> visited = Sets.newHashSet();
            Set<EntityRef> toVisit = Sets.newHashSet(startEntityRef);
            while (toVisit.size() != 0) {
                Set<EntityRef> temp = Sets.newHashSet();
                for (EntityRef vist : toVisit) {
                    BlockComponent blockComponent = vist.getComponent(BlockComponent.class);
                    CableComponent cableComponent = vist.getComponent(CableComponent.class);
                    for (Side side : SideBitFlag.getSides(cableComponent.connections)) {
                        Vector3i loc = new Vector3i(blockComponent.getPosition()).add(side.getVector3i());
                        EntityRef nextEntityRef = blockEntityRegistry.getBlockEntityAt(loc);
                        if (nextEntityRef.hasComponent(LeafNodeComponent.class)) {
                            entries.put(nextEntityRef, (byte) (entries.getOrDefault(nextEntityRef, (byte) 0) + SideBitFlag.getSide(side.reverse())));
                        } else if (nextEntityRef.hasComponent(CableComponent.class)) {
                            if (!visited.contains(nextEntityRef)) {
                                temp.add(nextEntityRef);
                                visited.add(nextEntityRef);
                            }
                        }
                    }
                }
                toVisit = temp;
            }
        }

        return entries;
    }

    public Map<EntityRef, Integer> findDistanceFromLeafToAllOtherLeafs(Vector3i location, Side side) {
        Map<EntityRef, Integer> result = Maps.newLinkedHashMap();
        EntityRef startEntityRef = blockEntityRegistry.getBlockEntityAt(location);

        TreeMap<Integer, Set<EntityRef>> orderCollection = new TreeMap<>();
        orderCollection.put(1, Sets.newHashSet(blockEntityRegistry.getBlockEntityAt(new Vector3i(location).add(side.getVector3i()))));

        Set<EntityRef> visited = Sets.newHashSet(startEntityRef);
        do {
            int minimum = orderCollection.firstKey();
            Set<EntityRef> entries = orderCollection.get(minimum);
            for (EntityRef entry : entries) {
                CableComponent cableComponent = entry.getComponent(CableComponent.class);
                BlockComponent blockComponent = entry.getComponent(BlockComponent.class);
                for (Side s : SideBitFlag.getSides(cableComponent.connections)) {
                    Vector3i loc = new Vector3i(blockComponent.getPosition()).add(s.getVector3i());
                    EntityRef nextEntityRef = blockEntityRegistry.getBlockEntityAt(loc);
                    if (nextEntityRef.hasComponent(LeafNodeComponent.class)) {
                        result.put(nextEntityRef, minimum);
                    } else if (nextEntityRef.hasComponent(CableComponent.class)) {
                        if (!visited.contains(nextEntityRef)) {
                            Set<EntityRef> entrySet = orderCollection.putIfAbsent(minimum + 1, Sets.newHashSet());
                            entrySet.add(nextEntityRef);
                            visited.add(nextEntityRef);
                        }
                    }

                }
            }
            orderCollection.remove(minimum);
        }
        while (orderCollection.size() > 0);

        return result;
    }

}
