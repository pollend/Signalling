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
package org.terasology.network;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.terasology.math.Side;
import org.terasology.math.geom.Vector3i;

import java.util.Map;
import java.util.Set;

public class DistanceCache {
    private Map<Vector3i,LeafNodeEntry> leafNodeEntryMap = Maps.newHashMap();

    public void update(Vector3i loc){

    }

    public static LeafNodeDistance getDistances(Vector3i location, Side side){
        Set<LeafNodeDistance> entries = Sets.newHashSet();

        return null;
    }

    public static class LeafNodeEntry{
        Map<Side,Set<LeafNodeDistance>> nodeDistanceMap = Maps.newHashMap();
        public Iterable<LeafNodeDistance> getNodeDistances(Side side){
            return nodeDistanceMap.get(side);
        }

        public Iterable<Side> getSides(){
            return nodeDistanceMap.keySet();
        }

    }

    public static class LeafNodeDistance{
        private Vector3i location;
        private int distance;
        private Side input;
        public LeafNodeDistance(Vector3i location, int distance,Side input){
            this.location = location;
            this.distance = distance;
            this.input = input;
        }

        public int getDistance() {
            return distance;
        }

        public Vector3i getLocation() {
            return location;
        }

        public Side getInput() {
            return input;
        }
    }
}
