package net.tourbook.map25.layer.labeling;

import org.oscim.layers.tile.MapTile.TileData;
import org.oscim.renderer.bucket.SymbolItem;
import org.oscim.renderer.bucket.TextItem;

/**
 * Original: {@link org.oscim.layers.tile.vector.labeling.LabelTileData}
 */
public class LabelTileData extends TileData {
    public final List<SymbolItem> symbols = new List<SymbolItem>();
    public final List<TextItem> labels = new List<TextItem>();

    @Override
    protected void dispose() {
        TextItem.pool.releaseAll(labels.clear());
        SymbolItem.pool.releaseAll(symbols.clear());
    }
}
