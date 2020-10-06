package app.bean;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FileInfo {
    private String code;
    private String fileName;
    private int size;

    @Override
    public String toString() {
        return "FileInfo{" +
                "code='" + code + '\'' +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                '}';
    }
}
