# Cartoon snake skins

![All five cartoon snake skins](snake-skins-preview.png)

Choose a skin in the main menu; its preview updates immediately. Orange Pop is the default. Restarting a round with Tab keeps the selected skin.

- **Orange Pop:** bright tangerine, golden highlights, warm dark outlines, a smiling face and large glossy eyes.
- **Mr. Bones:** a friendly ivory skull, turquoise eyes, rounded ribs and a spine over a dark violet silhouette. The silhouette keeps the skeleton readable against the pale board.

- **Cosmic:** purple nebula swirls, mint stars, pink highlights and starry eyes.
- **Candy Pop:** bubblegum pink with mint candy stripes, creamy yellow accents and glossy jellybean eyes.
- **Jade Dragon:** jade scales, gold markings, rounded horns and short whiskers contained within each tile.

All five skins use transparent, cached 128px sprites drawn with JavaFX, including matching heads, straight bodies, corners and tapered tails. Sprites scale with the board and require no additional image files or dependencies. Artwork is cosmetic and does not change collision rules.

## Development

The project currently depends on JavaFX 25.0.1, which requires JDK 23 or later at runtime. Run `mvn package` and `mvn javafx:run` with a compatible JDK. This change preserves the existing dependency versions.
