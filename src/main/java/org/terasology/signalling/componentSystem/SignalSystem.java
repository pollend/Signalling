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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terasology.blockNetwork.BlockNetworkUtil;
import org.terasology.blockNetwork.EfficientBlockNetwork;
import org.terasology.blockNetwork.Network2;
import org.terasology.blockNetwork.NetworkChangeReason;
import org.terasology.engine.Time;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.entitySystem.systems.UpdateSubscriberSystem;
import org.terasology.logic.config.ModuleConfigManager;
import org.terasology.logic.health.BeforeDestroyEvent;
import org.terasology.math.Side;
import org.terasology.math.SideBitFlag;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.In;
import org.terasology.signalling.components.CableComponent;
import org.terasology.signalling.components.LeafNodeComponent;
import org.terasology.signalling.components.SignalConductorComponent;
import org.terasology.signalling.components.SignalConsumerAdvancedStatusComponent;
import org.terasology.signalling.components.SignalConsumerComponent;
import org.terasology.signalling.components.SignalConsumerStatusComponent;
import org.terasology.signalling.components.SignalProducerComponent;
import org.terasology.world.BlockEntityRegistry;
import org.terasology.world.WorldProvider;
import org.terasology.world.block.BeforeDeactivateBlocks;
import org.terasology.world.block.BlockComponent;
import org.terasology.world.block.OnActivatedBlocks;
import org.terasology.world.block.items.OnBlockItemPlaced;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@RegisterSystem(value = RegisterMode.AUTHORITY)
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


    /**
     * Adds the placed block to the correct list
     *
     * @param event     The event triggered by block placement
     * @param entityRef The entity information of the placed block
     */
    @ReceiveEvent()
    public void onBlockPlaced(OnBlockItemPlaced event, EntityRef entityRef) {
        EntityRef ref = event.getPlacedBlock();
        final Vector3i location = event.getPosition();
        if (ref.hasComponent(CableComponent.class)) {

        } else if (ref.hasComponent(LeafNodeComponent.class)) {

        }

    }

    @ReceiveEvent(components = {LeafNodeComponent.class})
    public void onLeafRemoved(BeforeDestroyEvent event, EntityRef block) {

    }

    @ReceiveEvent(components = {CableComponent.class})
    public void onCableRemoved(BeforeDestroyEvent event, EntityRef block) {

    }

    public void setOutput(EntityRef entityRef, Side side, long delay,boolean isActive) {

    }

}
