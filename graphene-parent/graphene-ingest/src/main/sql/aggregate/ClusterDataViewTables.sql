/**
 * Copyright (c) 2013 Oculus Info Inc. 
 * http://www.oculusinfo.com/
 * 
 * Released under the MIT License.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

-- -----------------------------
-- Influent Data Views 1.0 DRAFT
-- -----------------------------

--
--- Cluster membership dataview - note this view supports hierarchical clustering -  note: fuzzy cluster membership is not supported
---   table is denormalized with no referential integrity for simplicity of integration
--
CREATE TABLE GLOBAL_CLUSTER_DATAVIEW_1_00
(
clusterid nvarchar(100),  					-- using nvarchar for uid rather than bigint to support guids and other uid schemes
rootid nvarchar(100), 						-- id of root cluster in hierarchy - NULL if this cluster is a root level cluster
parentid nvarchar(100), 					-- id of parent cluster in hierarchy - NULL if this cluster is a root level cluster
hierarchylevel int NOT NULL DEFAULT 0,		-- level of cluster in hierarchy - level 0 is the root and level N ( N > 0 ) is a leaf cluster
entityid nvarchar(100) NOT NULL  			-- entity id of cluster member (entity id's are the id's of raw entities that are clustered)
);

--
--- Cluster membership dataview - note this view supports hierarchical clustering -  note: fuzzy cluster membership is not supported
---   table is denormalized with no referential integrity for simplicity of integration
--
CREATE TABLE DYNAMIC_CLUSTER_DATAVIEW_1_00
(
clusterid nvarchar(100),  									-- using nvarchar for uid rather than bigint to support guids and other uid schemes
globalclusterid nvarchar(100),  							-- the global cluster this dynamic cluster is a based upon
contextid nvarchar(100),									-- unique identifier for the context these dynamic cluster views belong to
modifiedDate datetime default CURRENT_TIMESTAMP NOT NULL, 	-- date this dynamic cluster member was last modified
rootid nvarchar(100), 										-- id of root cluster in hierarchy - NULL if this cluster is a root level cluster
parentid nvarchar(100), 									-- id of parent cluster in hierarchy - NULL if this cluster is a root level cluster
hierarchylevel int NOT NULL DEFAULT 0,						-- level of cluster in hierarchy - level 0 is the root and level N ( N > 0 ) is a leaf cluster
entityid nvarchar(100) NOT NULL  							-- entity id of cluster member (entity id's are the id's of raw entities that are clustered)
);

-- 
-- indexes for fast data retrieval
--
CREATE INDEX ix_gcluster_dataview_id ON GLOBAL_CLUSTER_DATAVIEW_1_00 (clusterid);
CREATE INDEX ix_gcluster_dataview_eid ON GLOBAL_CLUSTER_DATAVIEW_1_00 (entityid);
CREATE INDEX ix_gcluster_dataview_pid ON GLOBAL_CLUSTER_DATAVIEW_1_00 (parentid);
CREATE INDEX ix_gcluster_dataview_type ON GLOBAL_CLUSTER_DATAVIEW_1_00 (rootid, clustertype);
CREATE INDEX ix_gcluster_dataview_rid_lvl ON GLOBAL_CLUSTER_DATAVIEW_1_00 (rootid, hierarchylevel);
CREATE INDEX ix_dcluster_dataview_id ON DYNAMIC_CLUSTER_DATAVIEW_1_00 (clusterid);
CREATE INDEX ix_dcluster_dataview_eid ON DYNAMIC_CLUSTER_DATAVIEW_1_00 (entityid);
CREATE INDEX ix_dcluster_dataview_gid ON DYNAMIC_CLUSTER_DATAVIEW_1_00 (globalclusterid);
CREATE INDEX ix_dcluster_dataview_pid ON DYNAMIC_CLUSTER_DATAVIEW_1_00 (parentid);
CREATE INDEX ix_dcluster_dataview_type ON DYNAMIC_CLUSTER_DATAVIEW_1_00 (rootid, clustertype);
CREATE INDEX ix_dcluster_dataview_rid_lvl ON DYNAMIC_CLUSTER_DATAVIEW_1_00 (rootid, hierarchylevel);