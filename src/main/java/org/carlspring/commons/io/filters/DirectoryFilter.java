package org.carlspring.commons.io.filters;

import java.io.File;
import java.io.FileFilter;

/**
 * A directory filter which supports including/excluding hidden directories.
 
 * @author mtodorov
 */
public class DirectoryFilter
        implements FileFilter
{

    private boolean excludeHiddenDirectories = true;


    public DirectoryFilter()
    {
    }

    public DirectoryFilter(boolean excludeHiddenDirectories)
    {
        this.excludeHiddenDirectories = excludeHiddenDirectories;
    }

    @Override
    public boolean accept(File file)
    {
        return file.isDirectory() && (!excludeHiddenDirectories || !file.getName().matches(".*//*\\..*"));
    }

    public boolean excludeHiddenDirectories()
    {
        return excludeHiddenDirectories;
    }

    public void setExcludeHiddenDirectories(boolean excludeHiddenDirectories)
    {
        this.excludeHiddenDirectories = excludeHiddenDirectories;
    }

}
