package net.replaceitem.symbolchat.extensions;

import net.minecraft.client.gui.ScreenRect;

public interface DrawContextExtension {
    boolean scissorOverlaps(ScreenRect rect);
    
    interface ScissorStackExtension {
        boolean overlaps(ScreenRect rect);
    }
}
