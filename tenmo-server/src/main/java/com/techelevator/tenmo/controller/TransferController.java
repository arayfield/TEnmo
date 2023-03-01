package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.services.TransferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/transfers")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }
    /**
     * Retrieves a list of all transfers.
     * @return A ResponseEntity object containing a list of Transfer objects
     */
    @GetMapping
    public ResponseEntity<List<Transfer>> getAllTransfers() {
        List<Transfer> transfers = transferService.getAllTransfers();
        return ResponseEntity.ok(transfers);
    }
    /**
     * Retrieves a specific transfer by ID.
     *
     * @param transferId the ID of the transfer to retrieve
     * @return a ResponseEntity containing the Transfer object with the specified ID and a status code of 200 (OK), or a status code of 404 (Not Found) if the transfer is not found
     */
    @GetMapping("/{transferId}")
    public ResponseEntity<Transfer> getTransferById(@PathVariable int transferId) {
        Transfer transfer = transferService.getTransferById(transferId);
        if (transfer == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(transfer);
        }
    }
    /**
     * Retrieves a list of transfers associated with the specified user.
     *
     * @param userId the ID of the user whose transfers should be retrieved
     * @return a ResponseEntity containing a list of Transfer objects if the user exists, or a 404 Not Found status otherwise
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<Transfer>> getTransfersByUserId(@PathVariable int userId) {
        List<Transfer> transfers = transferService.getTransfersByUserId(userId);
        return ResponseEntity.ok(transfers);
    }
    /**
     * Creates a new transfer based on the provided Transfer object.
     *
     * @param transfer The Transfer object to create.
     * @return A ResponseEntity containing the created Transfer object.
     */
    @PostMapping
    public ResponseEntity<Transfer> createTransfer(@Valid @RequestBody Transfer transfer) {
        transferService.createTransfer(transfer);
        return new ResponseEntity<>(transfer, HttpStatus.CREATED);
    }
    /**
     * Updates an existing transfer based on the provided Transfer object.
     *
     * @param transfer The Transfer object to update.
     * @return A ResponseEntity containing the updated Transfer object.
     */
    @PutMapping("/{transferId}")
    public ResponseEntity<Transfer> updateTransfer(@Valid @RequestBody Transfer transfer, @PathVariable long transferId) {
        // Make sure the transfer ID from the path variable matches the transfer ID in the request body
        if (transfer.getTransferId() != transferId) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer ID in path variable does not match Transfer ID in request body");
        }

        // Update the transfer in the database using the TransferService
        transferService.updateTransfer(transfer);

        // Retrieve the updated transfer from the database and return it in the response body
        Transfer updatedTransfer = transferService.getTransferById(transferId);
        return ResponseEntity.ok(updatedTransfer);
    }
    /**
     * Deletes the transfer with the specified ID.
     *
     * @param transferId the ID of the transfer to delete
     */
    @DeleteMapping("/{transferId}")
    public void deleteTransfer(@PathVariable long transferId) {
        transferService.deleteTransfer(transferId);
    }
}