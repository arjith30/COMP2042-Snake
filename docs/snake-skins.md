# Cartoon snake skins

![Orange Pop and Mr. Bones artwork](snake-skins-preview.png)

Choose a skin in the main menu; its preview updates immediately. Orange Pop is the default. Restarting a round with Tab keeps the selected skin.

- **Orange Pop:** bright tangerine, golden highlights, warm dark outlines, a smiling face and large glossy eyes.
- **Mr. Bones:** a friendly ivory skull, turquoise eyes, rounded ribs and a spine over a dark violet silhouette. The silhouette keeps the skeleton readable against the pale board.

Both skins use transparent, cached 128px sprites drawn with JavaFX, including matching heads, straight bodies, corners and tapered tails. Sprites scale with the board and require no additional image files or dependencies. Artwork is cosmetic and does not change collision rules.

## Three recommended future skins

1. **Cosmic:** a deep purple body with cyan and pink nebula swirls, oversized starry eyes and scattered chunky star markings. Keep the glow subtle so the silhouette stays clear.
2. **Candy Pop:** bubblegum pink and turquoise candy stripes, a creamy yellow belly, sprinkle markings and round jellybean eyes. Broad stripes will stay readable at the game's 20px tile size.
3. **Jade Dragon:** vivid jade scales, a golden belly, tiny rounded horns, short whiskers and a playful expression. Keep decorative features within each tile so the artwork agrees with collision boundaries.

These three are recommendations; the two requested skins are implemented.

## Development

The project currently depends on JavaFX 25.0.1, which requires JDK 23 or later at runtime. Run `mvn package` and `mvn javafx:run` with a compatible JDK. This change preserves the existing dependency versions.
