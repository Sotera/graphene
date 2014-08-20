drop table G_USER_GROUP;
drop table G_USER_WORKSPACE;
drop table G_GROUP;
drop table G_WORKSPACE;
drop table G_USER;


--We call the table G_USER because 'user' is a keyword in TSQL, and I didn't want developer to have to deal with knowing to put it in brackets.
create table G_USER (
id int not null auto_increment PRIMARY KEY,
username VARCHAR(50),
active bit,
email VARCHAR(100) ,
salt VARCHAR(128),
hashedpassword VARCHAR(128),
created datetime,
modified datetime,
lastlogin datetime, 
numberlogins int,
avatar VARCHAR(100),
fullname VARCHAR(100)
);


--defines a group
CREATE TABLE G_GROUP (
    id int not null auto_increment PRIMARY KEY,
    name VARCHAR(200),
    description TEXT
);

--defines a workspace
--note: not a reference on purpose.  If we delete a user we don't want the workspace to disappear by cascading.
create table G_WORKSPACE (
 id int not null auto_increment PRIMARY KEY,
 title VARCHAR(100),
 created datetime,
 modified datetime,
 description TEXT,
 json TEXT,
 queries TEXT,
 map TEXT
);
--ties usernames to GROUP
CREATE TABLE G_USER_GROUP (
  user_id int REFERENCES G_USER(id),
  group_id int REFERENCES G_GROUP(id),
  role VARCHAR(200)
);

--ties usernames to workspaces
create table G_USER_WORKSPACE (
  user_id int  REFERENCES G_USER(id),
  workspace_id int  REFERENCES G_WORKSPACE(id),
  role VARCHAR(200)
);


--CREATE TABLE g_permission (
--    permissionname VARCHAR PRIMARY KEY,
--    defaultpolicy BOOLEAN );
--
--CREATE TABLE g_group_permission (
--    permissionname VARCHAR PRIMARY KEY REFERENCES g_permission(permissionname),
--    groupname VARCHAR PRIMARY KEY REFERENCES G_GROUP(groupname),
--    priority INTEGER,
--    policy BOOLEAN );
    
    

    
--    (
--SELECT policy, priority 
--FROM g_group_permission
--JOIN G_USER_GROUP ON G_USER_GROUP.groupname = g_group_permissions.groupname
--WHERE username = :username
--AND permissionname = :perm
--
--UNION
--
--SELECT 
--    defaultpolicy as policy,
--    (SELECT min(priority) - 1 FROM g_group_permission) as priority
--FROM g_permission
--WHERE permissionname = :perm
--)
--ORDER BY priority DESC
--LIMIT 1



