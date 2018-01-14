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
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.Share;
import org.terasology.signalling.components.CableComponent;
import org.terasology.signalling.components.SignalLeafComponent;
import org.terasology.registry.In;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.family.MultiConnectFamily;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@RegisterSystem(RegisterMode.AUTHORITY)
@Share(value = NetworkSystem.class)
public class NetworkSystem extends BaseComponentSystem {
    @In
    private BlockEntityRegistry blockEntityRegistry;

    public byte getConnections(EntityRef entityRef) {
        BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);
        if (blockComponent == null)
            return 0;
        Block block = blockComponent.getBlock();

        if (entityRef.hasComponent(SignalLeafComponent.class)) {
            SignalLeafComponent leafNodeComponent = entityRef.getComponent(SignalLeafComponent.class);
            byte result = (byte) 0;
            for (Side side : SideBitFlag.getSides(leafNodeComponent.connections)) {
                result |= SideBitFlag.getSide(block.getRotation().rotate(side));
            }
            return result;

        } else if (entityRef.hasComponent(CableComponent.class)) {
            if (block.getBlockFamily() instanceof MultiConnectFamily) {
                return ((MultiConnectFamily) block.getBlockFamily()).getConnections(block.getURI());
            }
        }
        return 0;
    }

    public byte getLeafInputConnection(EntityRef entityRef) {
        BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);
        if (blockComponent == null)
            return 0;
        Block block = blockComponent.getBlock();

        if (entityRef.hasComponent(SignalLeafComponent.class)) {
            SignalLeafComponent leafNodeComponent = entityRef.getComponent(SignalLeafComponent.class);
            byte result = (byte) 0;
            for (Side side : SideBitFlag.getSides(leafNodeComponent.inputs)) {
                result |= SideBitFlag.getSide(block.getRotation().rotate(side));
            }
            return result;
        }
        return 0;
    }

    public byte getLeafOutputConnection(EntityRef entityRef) {
        BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);
        if (blockComponent == null)
            return 0;
        Block block = blockComponent.getBlock();

        if (entityRef.hasComponent(SignalLeafComponent.class)) {
            SignalLeafComponent leafNodeComponent = entityRef.getComponent(SignalLeafComponent.class);
            byte result = (byte) 0;
            for (SignalLeafComponent.OuputMapping outputMapping : leafNodeComponent.outputs) {
                if (outputMapping.strength != 0)
                    result |= SideBitFlag.getSide(block.getRotation().rotate(outputMapping.side));
            }
            return result;
        }
        return 0;
    }

//    public Map<EntityRef, Byte> findLeafNodesWithSide(Vector3i location) {
//        Map<EntityRef, Byte> entries = Maps.newHashMap();
//        EntityRef startEntityRef = blockEntityRegistry.getBlockEntityAt(location);
//        if (startEntityRef.hasComponent(SignalLeafComponent.class)) {
//            SignalLeafComponent leafNodeComponent = startEntityRef.getComponent(SignalLeafComponent.class);
//            entries.put(startEntityRef, this.getConnections(startEntityRef));
//            return entries;
//        }
//        if (startEntityRef.hasComponent(CableComponent.class)) {
//            Set<EntityRef> visited = Sets.newHashSet();
//            Set<EntityRef> toVisit = Sets.newHashSet(startEntityRef);
//            while (toVisit.size() != 0) {
//                Set<EntityRef> temp = Sets.newHashSet();
//                for (EntityRef vist : toVisit) {
//                    BlockComponent blockComponent = vist.getComponent(BlockComponent.class);
//                    for (Side side : SideBitFlag.getSides(getConnections(vist))) {
//                        Vector3i loc = new Vector3i(blockComponent.getPosition()).add(side.getVector3i());
//                        EntityRef nextEntityRef = blockEntityRegistry.getBlockEntityAt(loc);
//                        if (nextEntityRef.hasComponent(SignalLeafComponent.class)) {
//                            entries.put(nextEntityRef, (byte) (entries.getOrDefault(nextEntityRef, (byte) 0) + SideBitFlag.getSide(side.reverse())));
//                        } else if (nextEntityRef.hasComponent(CableComponent.class)) {
//                            if (!visited.contains(nextEntityRef)) {
//                                temp.add(nextEntityRef);
//                                visited.add(nextEntityRef);
//                            }
//                        }
//                    }
//                }
//                toVisit = temp;
//            }
//        }
//
//        return entries;
//    }

    public Map<EntityRef, Set<SideDistance>> findDistanceFromLeafToAllOtherLeafs(Vector3i location, Side side) {
        Map<EntityRef, Set<SideDistance>> result = Maps.newLinkedHashMap();
//        EntityRef startEntityRef = blockEntityRegistry.getBlockEntityAt(location);

        TreeMap<Integer, Set<Vector3i>> orderCollection = new TreeMap<>();
        orderCollection.put(1, Sets.newHashSet(new Vector3i(location).add(side.getVector3i())));

        Set<Vector3i> visited = Sets.newHashSet(location);
        do {
            int minimum = orderCollection.firstKey();
            Set<Vector3i> entries = orderCollection.get(minimum);
            for (Vector3i entry : entries) {
                EntityRef ref = blockEntityRegistry.getBlockEntityAt(entry);
                BlockComponent blockComponent = ref.getComponent(BlockComponent.class);
                for (Side s : SideBitFlag.getSides(getConnections(ref))) {
                    Vector3i loc = new Vector3i(blockComponent.getPosition()).add(s.getVector3i());
                    EntityRef nextEntityRef = blockEntityRegistry.getBlockEntityAt(loc);
                    if (nextEntityRef.hasComponent(SignalLeafComponent.class)) {
                        if (!visited.contains(loc)) {
                            Set<SideDistance> sideDistances = result.getOrDefault(nextEntityRef, Sets.newHashSet());
                            sideDistances.add(new SideDistance(s.reverse(), minimum));
                            result.put(nextEntityRef, sideDistances);
                        }
                    } else if (nextEntityRef.hasComponent(CableComponent.class)) {
                        if (!visited.contains(loc)) {
                            orderCollection.putIfAbsent(minimum + 1, Sets.newHashSet());
                            Set<Vector3i> entrySet = orderCollection.get(minimum + 1);
                            entrySet.add(loc);
                            visited.add(loc);
                        }
                    }

                }
            }
            orderCollection.remove(minimum);
        }
        while (orderCollection.size() > 0);

        return result;
    }

    public static class SideDistance {
        public final Side side;
        public final int distance;

        public SideDistance(Side side, int distance) {
            this.side = side;
            this.distance = distance;
        }
    }

}
