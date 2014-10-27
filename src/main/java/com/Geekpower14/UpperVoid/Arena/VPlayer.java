package com.Geekpower14.UpperVoid.Arena;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class VPlayer {

	public UUID uuid;

	public String name;

	public OfflinePlayer op;

	public VPlayer(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;

		op = Bukkit.getOfflinePlayer(uuid);
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public boolean hasPermission(String perm) {
		if (perm.equalsIgnoreCase(""))
			return true;
		if (op.isOp())
			return true;

		PermissionManager pex = PermissionsEx.getPermissionManager();
		PermissionUser user = pex.getUser(uuid);

		if (user.has("UpperVoid.admin"))
			return true;
		if (user.has(perm))
			return true;

		return false;
	}

}
