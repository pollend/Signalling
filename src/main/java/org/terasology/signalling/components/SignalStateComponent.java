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
package org.terasology.signalling.components;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import org.terasology.entitySystem.Component;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.math.Side;
import org.terasology.reflection.MappedContainer;

import java.util.List;

public class SignalStateComponent  implements Component {

    public static final ImmutableList<Side> OUTPUT_SIDES = ImmutableList.of(Side.LEFT,Side.FRONT,Side.RIGHT,Side.BACK,Side.TOP,Side.BOTTOM);
    public int[] outputs = new int[6];


//    public List<CacheEntry> cacheEntries = Lists.newArrayList();

//    @MappedContainer
//    public static class OuputMapping{
//        public Side side;
//        public int strength;
//        public OuputMapping(){
//        }
//        public OuputMapping(Side side, int strength){
//            this.side = side;
//            this.strength = strength;
//        }
//    }
//
//    @MappedContainer
//    public static class CacheEntry{
//        public Side remoteSide;
//        public EntityRef remoteEntity;
//
//        public CacheEntry(){
//        }
//        public CacheEntry(Side side, Side remoteSide, EntityRef remoteEntity){
//            this.remoteSide = remoteSide;
//            this.remoteEntity = remoteEntity;
//        }
//    }
}
