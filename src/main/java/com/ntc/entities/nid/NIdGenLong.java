/*
 * Copyright 2015 nghiatc.
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

package com.ntc.entities.nid;

/**
 *
 * @author nghiatc
 * @since Aug 20, 2015
 */
public class NIdGenLong {
    private String name;
    private long seq;

    private NIdGenLong(){}

    public NIdGenLong(String name) {
        this.name = name;
    }

    public NIdGenLong(String name, long seq) {
        this.name = name;
        this.seq = seq;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @Override
    public String toString() {
        return "NIdGenLong{" + "name=" + name + ", seq=" + seq + '}';
    }
}
