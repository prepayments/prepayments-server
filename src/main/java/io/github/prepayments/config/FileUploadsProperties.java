package io.github.prepayments.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configurations related to file-upload process
 */
@Configuration
@ConfigurationProperties(prefix = "reader")
@PropertySource(value = "classpath:config/fileUploads.yml", factory = FileUploadsPropertyFactory.class)
public class FileUploadsProperties {

    private int listSize;
    private int largeUploads;

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }

    public int getListSize() {
        return listSize;
    }

    public int getLargeUploads() {
        return largeUploads;
    }

    public void setLargeUploads(final int largeUploads) {
        this.largeUploads = largeUploads;
    }
}
