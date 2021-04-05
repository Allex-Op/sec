package pt.ulisboa.tecnico.sec.services.dto;

import java.util.List;

public class SpecialUserResponseDTO {
    List<String> witnesses;

    public void setWitnesses(List<String> witnesses) {
        this.witnesses = witnesses;
    }

    public List<String> getWitnesses() {
        return witnesses;
    }
}
