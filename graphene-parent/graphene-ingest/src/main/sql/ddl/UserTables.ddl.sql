drop table g_user_groups;
drop table g_user_workspace;
drop table g_groups;
drop table g_workspace;
drop table g_user;


--We call the table g_user because 'user' is a keyword in TSQL, and I didn't want developer to have to deal with knowing to put it in brackets.
create table g_user (
username nvarchar(50) PRIMARY KEY,
active bit,
email nvarchar(100) ,
salt nvarchar(128),
hashedpassword nvarchar(128), --for SHA512 hash
accountcreated datetime,
lastlogin datetime, 
numberlogins integer,
avatar nvarchar(100),
fullname nvarchar(100)
);


--defines a group
CREATE TABLE g_groups (
    groupname VARCHAR PRIMARY KEY 
);


--defines a workspace
create table g_workspace (
 workspace_id int PRIMARY KEY,
 creator_username nvarchar(50), --note: not a reference on purpose.  If we delete a user we don't want the workspace to disappear by cascading.
 title nvarchar(100),
 json nvarchar(MAX)
);
--ties usernames to groups
CREATE TABLE g_user_groups (
    username nvarchar(50) FOREIGN KEY REFERENCES g_user(username),
    groupname VARCHAR FOREIGN KEY REFERENCES g_groups(groupname)
);

--ties usernames to workspaces
create table g_user_workspace (
  username nvarchar(50) FOREIGN KEY REFERENCES g_user(username),
  workspace_id int FOREIGN KEY REFERENCES g_workspace(workspace_id)
);


--CREATE TABLE g_permission (
--    permissionname VARCHAR PRIMARY KEY,
--    defaultpolicy BOOLEAN );
--
--CREATE TABLE g_group_permission (
--    permissionname VARCHAR PRIMARY KEY REFERENCES g_permission(permissionname),
--    groupname VARCHAR PRIMARY KEY REFERENCES g_groups(groupname),
--    priority INTEGER,
--    policy BOOLEAN );
    
    

    
--    (
--SELECT policy, priority 
--FROM g_group_permission
--JOIN g_user_groups ON g_user_groups.groupname = g_group_permissions.groupname
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



