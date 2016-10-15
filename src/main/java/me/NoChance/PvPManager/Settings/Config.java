package me.NoChance.PvPManager.Settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.NoChance.PvPManager.PvPManager;

public class Config extends YamlConfiguration {

	private final File file;
	private final FileConfiguration config;
	private final PvPManager plugin;

	@SuppressWarnings("deprecation")
	public Config(final PvPManager plugin, final String name) {
		this.plugin = plugin;
		final File file1 = new File(plugin.getDataFolder(), name);
		if (!file1.exists()) {
			this.prepareFile(file1, name);
		}
		this.file = file1;
		this.config = YamlConfiguration.loadConfiguration(this.getConfigContent(file1));
	}

	@Override
	public final void set(final String path, final Object value) {
		this.config.set(path, value);
	}

	@Override
	public final String getString(final String path) {
		return this.config.getString(path);
	}

	@Override
	public final String getString(final String path, final String def) {
		return this.config.getString(path, def);
	}

	@Override
	public final int getInt(final String path) {
		return this.config.getInt(path);
	}

	@Override
	public final int getInt(final String path, final int def) {
		return this.config.getInt(path, def);
	}

	@Override
	public final boolean getBoolean(final String path) {
		return this.config.getBoolean(path);
	}

	@Override
	public final boolean getBoolean(final String path, final boolean def) {
		return this.config.getBoolean(path, def);
	}

	@Override
	public final ConfigurationSection getConfigurationSection(final String path) {
		if (!config.isConfigurationSection(path))
			return config.createSection(path);
		return this.config.getConfigurationSection(path);
	}

	@Override
	public final double getDouble(final String path) {
		return this.config.getDouble(path);
	}

	@Override
	public final double getDouble(final String path, final double def) {
		return this.config.getDouble(path, def);
	}

	@Override
	public final List<?> getList(final String path) {
		return this.config.getList(path);
	}

	@Override
	public final List<?> getList(final String path, final List<?> def) {
		return this.config.getList(path, def);
	}

	private InputStream getConfigContent(final File file1) {
		if (!file1.exists())
			return null;
		try (BufferedReader reader = new BufferedReader(new FileReader(file1))) {
			int commentNum = 0;
			String addLine;
			String currentLine;
			final String pluginName = this.getPluginName();
			final StringBuilder whole = new StringBuilder("");
			while ((currentLine = reader.readLine()) != null)
				if (currentLine.startsWith("#")) {
					addLine = currentLine.replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
					whole.append(addLine).append("\n");
					commentNum++;

				} else {
					whole.append(currentLine).append("\n");
				}
			return new ByteArrayInputStream(whole.toString().getBytes(Charset.forName("UTF-8")));
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void prepareFile(final File file1, final String resource) {
		try {
			file1.getParentFile().mkdirs();
			file1.createNewFile();
			if (resource != null && !resource.isEmpty()) {
				this.copyResource(plugin.getResource(resource), file1);
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private void copyResource(final InputStream resource, final File file1) {
		try (OutputStream out = new FileOutputStream(file1)) {
			int lenght;
			final byte[] buf = new byte[1024];

			while ((lenght = resource.read(buf)) > 0) {
				out.write(buf, 0, lenght);
			}
			resource.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public final void saveConfig() {
		saveConfig(this.config.saveToString(), this.file);
	}

	private void saveConfig(final String configString, final File file1) {
		final String configuration = this.prepareConfigString(configString);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file1))) {
			writer.write(configuration);
			writer.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private String prepareConfigString(final String configString) {
		int lastLine = 0;
		int headerLine = 0;
		final String[] lines = configString.split("\n");
		final StringBuilder config1 = new StringBuilder("");
		for (final String line : lines)
			if (line.startsWith(this.getPluginName() + "_COMMENT")) {
				final String comment = "#" + line.trim().substring(line.indexOf(":") + 1);
				if (comment.startsWith("# +-")) {
					if (headerLine == 0) {
						config1.append(comment).append("\n");
						lastLine = 0;
						headerLine = 1;
					} else {
						config1.append(comment).append("\n\n");

						lastLine = 0;
						headerLine = 0;
					}
				} else {
					String normalComment;
					if (comment.startsWith("# ' ")) {
						normalComment = comment.substring(0, comment.length() - 1).replaceFirst("# ' ", "# ");
					} else {
						normalComment = comment;
					}
					if (lastLine == 0) {
						config1.append(normalComment).append("\n");
					} else {
						config1.append("\n").append(normalComment).append("\n");
					}
					lastLine = 0;
				}
			} else {
				config1.append(line).append("\n");
				lastLine = 1;
			}
		return config1.toString();
	}

	private String getPluginName() {
		return plugin.getDescription().getName();
	}

}
