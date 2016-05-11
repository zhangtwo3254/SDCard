package me.moqs.sdcard.modal;

/**
 * SDCard
 * <p/>
 * Created by User on 2016-05-11.
 */
public class SDCard {

    private boolean isDirectory; // 是否为目录
    private String name; // 文件名称
    private String size; // 文件大小(M)

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "SDCard{" +
                "isDirectory=" + isDirectory +
                ", name='" + name + '\'' +
                ", size=" + size +
                '}';
    }
}
