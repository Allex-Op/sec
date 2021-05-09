package pt.ulisboa.tecnico.sec.services.dto;

import java.util.Objects;

public class RequestDTO {

    private String clientId; // The sender

    private RequestLocationDTO requestLocationDTO;
    private ReportDTO reportDTO;
    private RequestUserProofsDTO requestUserProofsDTO;

    public RequestLocationDTO getRequestLocationDTO() {
        return requestLocationDTO;
    }

    public void setRequestLocationDTO(RequestLocationDTO requestLocationDTO) {
        this.requestLocationDTO = requestLocationDTO;
    }

    public ReportDTO getReportDTO() {
        return reportDTO;
    }

    public void setReportDTO(ReportDTO reportDTO) {
        this.reportDTO = reportDTO;
    }

    public RequestUserProofsDTO getRequestUserProofsDTO() {
        return requestUserProofsDTO;
    }

    public void setRequestUserProofsDTO(RequestUserProofsDTO requestUserProofsDTO) {
        this.requestUserProofsDTO = requestUserProofsDTO;
    }
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean encapsulationOfRequestLocationIsEmpty() {
        return requestLocationDTO == null;
    }

    public boolean encapsulationOfReportIsEmpty() {
        return reportDTO == null;
    }

    public boolean encapsulationOfRequestUserProofsIsEmpty() {
        return requestUserProofsDTO == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestDTO that = (RequestDTO) o;
        return clientId.equals(that.clientId) && requestLocationDTO.equals(that.requestLocationDTO) && reportDTO.equals(that.reportDTO) && requestUserProofsDTO.equals(that.requestUserProofsDTO);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, requestLocationDTO, reportDTO, requestUserProofsDTO);
    }
}
