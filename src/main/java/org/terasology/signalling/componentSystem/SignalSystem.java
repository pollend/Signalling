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

import com.google.common.collect.Maps;
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
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.signalling.components.CableComponent;
import org.terasology.signalling.components.SignalLeafComponent;
import org.terasology.signalling.event.LeafNodeSignalChange;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.items.OnBlockItemPlaced;

import java.util.Map;
import java.util.Set;


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


    /**
     * Adds the placed block to the correct list
     *
     * @param event     The event triggered by block placement
     * @param entityRef The entity information of the placed block
     */
    @ReceiveEvent()
    public void onBlockPlaced(OnBlockItemPlaced event, EntityRef entityRef) {
//        EntityRef ref = event.getPlacedBlock();
//        final Vector3i location = event.getPosition();
//        if (ref.hasComponent(CableComponent.class)) {
//
//        } else if (ref.hasComponent(SignalLeafComponent.class)) {
//
//        }

    }

    @ReceiveEvent(components = {SignalLeafComponent.class})
    public void onLeafRemoved(BeforeDestroyEvent event, EntityRef block) {

    }

    @ReceiveEvent(components = {CableComponent.class})
    public void onCableRemoved(BeforeDestroyEvent event, EntityRef block) {

    }


    public void updateAllNodesFromSide(EntityRef entityRef, Side side) {
        if (entityRef.hasComponent(SignalLeafComponent.class)) {
            BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);

            for (EntityRef entity : networkSystem.findDistanceFromLeafToAllOtherLeafs(blockComponent.getPosition(), side).keySet()) {
                updateNode(entity);
            }
        }
    }

    public int getOutputStrength(SignalLeafComponent leafComponent, Side side) {
        for (SignalLeafComponent.OuputMapping outputMapping : leafComponent.outputs) {
            if (outputMapping != null && outputMapping.side == side)
                return outputMapping.strength;
        }
        return 0;
    }

    public void setOutput(EntityRef entityRef, Side side, int strength) {
        SignalLeafComponent leafComponent = entityRef.getComponent(SignalLeafComponent.class);
        if (leafComponent == null)
            return;

        if (getOutputStrength(leafComponent, side) == strength)
            return;

        leafComponent.outputs.removeIf(ouputMapping ->
                ouputMapping == null || ouputMapping.side == side
        );
        leafComponent.outputs.add(new SignalLeafComponent.OuputMapping(side, strength));
        entityRef.saveComponent(leafComponent);
        updateAllNodesFromSide(entityRef, side);
    }

    public void updateNode(EntityRef entityRef) {
        BlockComponent blockComponent = entityRef.getComponent(BlockComponent.class);

        Map<Side, Integer> inputs = Maps.newHashMap();
        for (Side side : SideBitFlag.getSides(networkSystem.getLeafInputConnection(entityRef))) {
            int power = 0;
            for (Map.Entry<EntityRef, Set<NetworkSystem.SideDistance>> nodes : networkSystem.findDistanceFromLeafToAllOtherLeafs(blockComponent.getPosition(), side).entrySet()) {
                EntityRef ref = nodes.getKey();
                SignalLeafComponent leafNodeComponent = ref.getComponent(SignalLeafComponent.class);
                for (NetworkSystem.SideDistance distance : nodes.getValue()) {
                    int outputStrength = this.getOutputStrength(leafNodeComponent, distance.side);
                    if (outputStrength == -1) {
                        power = -1;
                        break;
                    }
                    int delta = outputStrength - distance.distance;
                    if (delta > 0 && delta > power) {
                        power = delta;
                    }
                }
            }
            inputs.put(side, power);
        }
        entityRef.send(new LeafNodeSignalChange(inputs));
    }

}

