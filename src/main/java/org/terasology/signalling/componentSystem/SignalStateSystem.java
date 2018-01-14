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

import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.logic.health.BeforeDestroyEvent;
import org.terasology.signalling.components.CableComponent;
import org.terasology.signalling.components.SignalLeafComponent;
import org.terasology.world.block.items.OnBlockItemPlaced;

public class SignalStateSystem {


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
}
