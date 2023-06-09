package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferHistoryDto;
import com.techelevator.tenmo.model.TransferStatus;
import com.techelevator.util.BasicLogger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
/**
 * The TransferService class provides methods for interacting with the Transfer resource on the Tenmo API server.
 * It communicates with the server over HTTP using a RestTemplate object.
 *
 * @author Mebrahtu
 */
public class TransferService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;

    private String authToken = null;

    /**
     * Constructs a new TransferService with the specified RestTemplate.
     *
     * @param url the baseUrl for the API
     */
    public TransferService(String url) {
        this.baseUrl = url + "transfers";
    }

    public void setCurrentUser(AuthenticatedUser currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Gets all transfers from the API server.
     *
     * @return an array of Transfer objects representing all transfers
     */
    public TransferHistoryDto[] getAllTransfersByUser() {
        TransferHistoryDto[] history = null;

        try{
            long userId = currentUser.getUser().getId();

            ResponseEntity<TransferHistoryDto[]> response = restTemplate.exchange(baseUrl + "/users/" + userId, HttpMethod.GET,
                    makeAuthEntity(currentUser), TransferHistoryDto[].class);
            history = response.getBody();
        } catch (RestClientResponseException e){
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return history;
    }
    /**
     * Gets a transfer with the specified ID from the API server.
     *
     * @param transferId the ID of the transfer to get
     * @return the Transfer object representing the transfer with the specified ID
     */
    public Transfer getTransferById(long transferId) {
        Transfer transfer = null;
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(baseUrl + "/" + transferId,
             HttpMethod.GET, makeAuthEntity(currentUser), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException e){
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }
    /**
     * Creates a new transfer on the API server.
     *
     *
     * @return true if the transfer was created successfully, false otherwise
     */
    public boolean createTransfer(long userTo, BigDecimal amount) {

        boolean success = false;
        long userFrom = currentUser.getUser().getId();
        String url = baseUrl + "/new?userFrom=" + userFrom + "&userTo=" + userTo +"&amount=" + amount;

        try{
            restTemplate.postForObject(url, makeAuthEntity(currentUser), Void.class);
            success = true;
        } catch (RestClientResponseException e){
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return success;
    }
    /**
     * Updates an existing transfer on the API server.
     *
     * @param transfer the Transfer object representing the transfer to update
     * @param status the status of the transfer
     * @return true if the transfer was updated successfully, false otherwise
     */
    public boolean updateTransfer(Transfer transfer, TransferStatus status) {
        boolean success = false;
        transfer.setTransferStatusId(status.getTransferStatusId());
        HttpEntity<Transfer> entity = makeEntity(transfer);
        try{
            restTemplate.put(baseUrl + transfer.getTransferId(), entity);
            success = true;
        } catch (RestClientResponseException e){
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return success;
    }
    /**
     * Deletes a transfer with the specified ID from the API server.
     *
     * @param transferId the ID of the transfer to delete
     * @return true if the transfer was deleted successfully, false otherwise
     */
    public boolean deleteTransfer(long transferId) {
        boolean success = false;
        try{
            restTemplate.delete(baseUrl + "/" + transferId);
            success = true;
        } catch (RestClientResponseException e){
            BasicLogger.log(e.getRawStatusCode() + " : " + e.getStatusText());
        } catch (ResourceAccessException e){
            BasicLogger.log(e.getMessage());
        }
        return success;
    }
    /**
     * Creates a new HttpEntity for the specified Transfer object with content type application/json.
     *
     * @param transfer the Transfer object to create an HttpEntity for
     * @return the HttpEntity for the specified Transfer object
     */
    private HttpEntity<Transfer> makeEntity(Transfer transfer){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, headers);
    }

    /**
     * Creates an HttpEntity with a bearer token belonging to the current user.
     *
     * @param currentUser An AuthenticatedUser that is currently logged into the app.
     * @return A new HttpEntity.
     * @author Dustin
     */
    private HttpEntity<Void> makeAuthEntity(AuthenticatedUser currentUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        return new HttpEntity<>(headers);
    }


}