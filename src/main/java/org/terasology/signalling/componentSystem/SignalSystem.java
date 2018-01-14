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
package org.terasology.signalling.componentSystem;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.config.ModuleConfigManager;
import org.terasology.logic.health.BeforeDestroyEvent;
import org.terasology.math.Rotation;
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.signalling.blockFamily.SignalCableBlockFamily;
import org.terasology.signalling.components.CableComponent;
import org.terasology.signalling.components.SignalLeafComponent;
import org.terasology.signalling.components.SignalStateComponent;
import org.terasology.signalling.event.LeafNodeSignalChange;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.family.MultiConnectFamily;
import org.terasology.world.block.items.OnBlockItemPlaced;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


@RegisterSystem(value = RegisterMode.AUTHORITY)
@Share(value = SignalSystem.class)
public class SignalSystem extends BaseComponentSystem {
    private static final Logger logger = LoggerFactory.getLogger(SignalSystem.class);

    @In
    private Time time;
    @In
    private WorldProvider worldProvider;
    @In
    private BlockEntityRegistry blockEntityRegistry;
    @In
    private ModuleConfigManager moduleConfigManager;
    @In
    private NetworkSystem networkSystem;

    public byte getConnection(EntityRef entityRef, byte sides)
    {
        BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);
        if (blockComponent == null)
            return 0;
        Block block = blockComponent.getBlock();
        Vector3i location = blockComponent.getPosition();
        if (entityRef.hasComponent(SignalLeafComponent.class)) {
            byte result = (byte) 0;
            for (Side side : SideBitFlag.getSides(sides)) {
                Side rotatedSide = block.getRotation().rotate(side);
                EntityRef  neighborEntity = blockEntityRegistry.getBlockEntityAt(new Vector3i(location).add(rotatedSide.getVector3i()));
                if(neighborEntity.hasComponent(CableComponent.class) || neighborEntity.hasComponent(SignalLeafComponent.class))
                    result |= SideBitFlag.getSide(block.getRotation().rotate(side));
            }
            return result;
        }
        return 0;
    }

    public byte getActiveOutputs(EntityRef entityRef) {
        SignalStateComponent signalStateComponent = entityRef.getComponent(SignalStateComponent.class);
        if(signalStateComponent == null)
            return 0;
        byte outputs = 0;
        for(int x = 0; x < signalStateComponent.outputs.length; x++){
            if(signalStateComponent.outputs[x] != 0) {
                outputs |= SideBitFlag.getSide(SignalStateComponent.OUTPUT_SIDES.get(x));
            }
        }
        return getConnection(entityRef,outputs);
    }

    public byte getAllConnections(EntityRef entityRef)
    {
        if(entityRef.hasComponent(SignalLeafComponent.class)) {
            SignalLeafComponent leafNodeComponent = entityRef.getComponent(SignalLeafComponent.class);
            if (leafNodeComponent == null)
                return 0;
            return getConnection(entityRef, leafNodeComponent.connections);
        }
        else if(entityRef.hasComponent(CableComponent.class)) {
            BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);
            Block block = blockComponent.getBlock();
            if (block.getBlockFamily() instanceof MultiConnectFamily) {
                return ((MultiConnectFamily) block.getBlockFamily()).getConnections(block.getURI());
            }
        }
        return 0;
    }



    public void updateAllNodesFromSide(EntityRef entityRef, Side side) {
//        if (entityRef.hasComponent(SignalLeafComponent.class)) {
//            BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);
//
//            for (EntityRef entity : networkSystem.findDistanceFromLeafToAllOtherLeafs(blockComponent.getPosition(), side).keySet()) {
//                updateNode(entity);
//            }
//        }
    }

    public int getOutputStrength(SignalLeafComponent leafComponent, Side side) {
//        for (SignalLeafComponent.OuputMapping outputMapping : leafComponent.outputs) {
//            if (outputMapping != null && outputMapping.side == side)
//                return outputMapping.strength;
//        }
        return 0;
    }

    public void setOutput(EntityRef entityRef, Side side, int strength) {
//        SignalLeafComponent leafComponent = entityRef.getComponent(SignalLeafComponent.class);
//        if (leafComponent == null)
//            return;
//
//        if (getOutputStrength(leafComponent, side) == strength)
//            return;
//
//        leafComponent.outputs.removeIf(ouputMapping ->
//                ouputMapping == null || ouputMapping.side == side
//        );
//        leafComponent.outputs.add(new SignalLeafComponent.OuputMapping(side, strength));
//        entityRef.saveComponent(leafComponent);
//        updateAllNodesFromSide(entityRef, side);
    }

    public void updateNode(EntityRef entityRef) {
//        BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);
//
//        Map<Side, Integer> inputs = Maps.newHashMap();
//        for (Side side : SideBitFlag.getSides(networkSystem.getLeafInputConnection(entityRef))) {
//            int power = 0;
//            for (Map.Entry<EntityRef, Set<NetworkSystem.SideDistance>> nodes : networkSystem.findDistanceFromLeafToAllOtherLeafs(blockComponent.getPosition(), side).entrySet()) {
//                EntityRef ref = nodes.getKey();
//                SignalLeafComponent leafNodeComponent = ref.getComponent(SignalLeafComponent.class);
//                for (NetworkSystem.SideDistance distance : nodes.getValue()) {
//                    int outputStrength = this.getOutputStrength(leafNodeComponent, distance.side);
//                    if (outputStrength == -1) {
//                        power = -1;
//                        break;
//                    }
//                    int delta = outputStrength - distance.distance;
//                    if (delta > 0 && delta > power) {
//                        power = delta;
//                    }
//                }
//            }
//            inputs.put(side, power);
//        }
//        entityRef.send(new LeafNodeSignalChange(inputs));
    }

    public Map<EntityRef, Set<SideDistance>> findDistanceFromLeafToAllOtherLeafs(Vector3i location, Side side) {
        Map<EntityRef, Set<SideDistance>> result = Maps.newLinkedHashMap();

        TreeMap<Integer, Set<Vector3i>> toVisit = new TreeMap<>();
        toVisit.put(1, Sets.newHashSet(new Vector3i(location).add(side.getVector3i())));

        Set<Vector3i> visited = Sets.newHashSet(location);
        do {
            int minimum = toVisit.firstKey();
            Set<Vector3i> entries = toVisit.get(minimum);
            for (Vector3i entry : entries) {
                EntityRef ref = blockEntityRegistry.getBlockEntityAt(entry);
                BlockComponent blockComponent = ref.getComponent(BlockComponent.class);
//                for (Side s : SideBitFlag.getSides(getConnections(ref))) {
//                    Vector3i loc = new Vector3i(blockComponent.getPosition()).add(s.getVector3i());
//                    EntityRef nextEntityRef = blockEntityRegistry.getBlockEntityAt(loc);
//                    if (!visited.contains(loc)) {
//                        if (nextEntityRef.hasComponent(SignalLeafComponent.class)) {
//                            Set<SideDistance> sideDistances = result.getOrDefault(nextEntityRef, Sets.newHashSet());
//                            sideDistances.add(new SideDistance(s.reverse(), minimum));
//                            result.put(nextEntityRef, sideDistances);
//                        } else if (nextEntityRef.hasComponent(CableComponent.class)) {
//                            toVisit.putIfAbsent(minimum + 1, Sets.newHashSet());
//                            Set<Vector3i> entrySet = toVisit.get(minimum + 1);
//                            entrySet.add(loc);
//                            visited.add(loc);
//                        }
//                    }
//                }
            }
            toVisit.remove(minimum);
        }
        while (toVisit.size() > 0);

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

