/*
 * Copyright (C) 2010 Douglas Teoh. Use is subject to license terms.
 */
package com.dteoh.treasuremap;

import java.util.LinkedList;
import java.util.List;

import org.jdesktop.application.ResourceMap;

/**
 * Convenience class for creating {@link ResourceMap}s.
 * 
 * @author Douglas Teoh
 * 
 */
public final class ResourceMaps {

    /**
     * Creates a {@link ResourceMap} containing all resources for the given
     * class.
     * 
     * Equivalent to calling:
     * <p>
     * <code>new ResourceMap(null, c.getClassLoader(), c.getPackage().getName()
     *  + ".resources." + c.getSimpleName());</code>
     * </p>
     * 
     * @param c
     *            Class to create the ResourceMap for.
     * @return The newly created ResourceMap.
     */
    public static ResourceMap create(final Class<?> c) {
        if (c == null) {
            throw new NullPointerException("Class cannot be null.");
        }

        ResourceMap rmap = new ResourceMap(null, c.getClassLoader(), c
                .getPackage().getName() + ".resources." + c.getSimpleName());
        return rmap;
    }

    /**
     * Creates a {@link ResourceMap} containing all resources for the given
     * class as well as the parent resource map.
     * 
     * Equivalent to calling:
     * <p>
     * <code>new ResourceMap(parent, c.getClassLoader(), c.getPackage().getName()
     *  + ".resources." + c.getSimpleName());</code>
     * </p>
     * 
     * @param parent
     *            Parent resource map.
     * @param c
     *            Class to create the ResourceMap for.
     * @return The newly created ResourceMap.
     */
    public static ResourceMap create(final ResourceMap parent, final Class<?> c) {
        if (c == null) {
            throw new NullPointerException("Class cannot be null.");
        }

        ResourceMap rmap = new ResourceMap(parent, c.getClassLoader(), c
                .getPackage().getName() + ".resources." + c.getSimpleName());
        return rmap;
    }

    /**
     * Create a ResourceMap that includes all resources for the given classes.
     * 
     * @param c
     *            Classes to create the ResourceMap for.
     * @return The newly created ResourceMap.
     */
    public static ResourceMap createMulti(final Class<?>... c) {
        return createMulti(null, c);
    }

    /**
     * Create a ResourceMap that includes all resources for the given classes as
     * well as the parent resource map.
     * 
     * @param parent
     *            Parent resource map.
     * @param c
     *            Classes to create the ResourceMap for.
     * @return The newly created ResourceMap.
     */
    public static ResourceMap createMulti(final ResourceMap parent,
            final Class<?>... c) {
        if (c == null || c.length == 0) {
            throw new NullPointerException("Classes cannot be null.");
        }

        ClassLoader loader = null;

        List<String> bundleNames = new LinkedList<String>();
        for (Class<?> bundle : c) {
            if (c == null) {
                continue;
            }
            loader = bundle.getClassLoader();
            bundleNames.add(bundle.getPackage().getName() + ".resources."
                    + bundle.getSimpleName());
        }

        ResourceMap rmap = new ResourceMap(parent, loader, bundleNames);
        return rmap;
    }
}
