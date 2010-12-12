/*
 * Copyright 2010 Roger Kapsi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ardverk.dht.storage;

import com.ardverk.dht.config.StoreConfig;

public interface DatabaseConfig {

    public boolean isStoreForward();
    
    public void setStoreForward(boolean storeForward);
    
    public boolean isCheckBucket();
    
    public void setCheckBucket(boolean checkBucket);
    
    public StoreConfig getStoreConfig();

    public void setStoreConfig(StoreConfig storeConfig);
}