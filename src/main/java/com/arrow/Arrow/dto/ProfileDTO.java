package com.arrow.Arrow.dto;

import lombok.*;

import java.io.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ProfileDTO implements Serializable {
    private String fullName;
    private String email;
    private Long phone;
    private String pan;
    private String bankAccountNumber;
    private String ifsc;
    private String aadhaar;

    private String username;

    public static ProfileDTO deserialize(byte[] serializedData) {
        ProfileDTO profileDTO = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(serializedData);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            // Deserialize the object
            profileDTO = (ProfileDTO) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Handle exceptions
            e.printStackTrace();
        }
        return profileDTO;
    }

    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this);
            return bos.toByteArray();
        }
    }


}
