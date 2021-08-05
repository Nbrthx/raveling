package eu.org.niberthix;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import org.bukkit.util.Consumer;

public class UpdateChecker {
	private Raveling plugin;
    private int resourceId;

    public UpdateChecker(Raveling plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void getVersion(final Consumer<String> consumer) {
        plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream();
            		Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.plugin.getServer().getConsoleSender().sendMessage("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}
