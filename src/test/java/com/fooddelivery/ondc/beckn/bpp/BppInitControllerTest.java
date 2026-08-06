package com.fooddelivery.ondc.beckn.bpp;

import com.fooddelivery.ondc.dto.OndcAckResponse;
import com.fooddelivery.ondc.dto.OndcContext;
import com.fooddelivery.ondc.dto.OndcRequest;
import com.fooddelivery.ondc.entity.OndcTransaction;
import com.fooddelivery.ondc.repository.OndcTransactionRepository;
import com.fooddelivery.ondc.util.OndcSchemaValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

class BppInitControllerTest {

    private BppInitController bppInitController;
    private OndcSchemaValidator schemaValidator;
    private OndcTransactionRepository transactionRepository;
    private BppCallbackService callbackService;

    @BeforeEach
    void setUp() {
        schemaValidator = Mockito.mock(OndcSchemaValidator.class);
        transactionRepository = Mockito.mock(OndcTransactionRepository.class);
        callbackService = Mockito.mock(BppCallbackService.class);
        bppInitController = new BppInitController(schemaValidator, transactionRepository, callbackService);
    }

    @Test
    void testInit() {
        // Arrange
        OndcRequest request = new OndcRequest();
        OndcContext context = new OndcContext();
        context.setTransactionId("txn-123");
        context.setMessageId("msg-456");
        context.setBapId("bap-id");
        context.setBppId("bpp-id");
        request.setContext(context);

        // Act
        ResponseEntity<OndcAckResponse> response = bppInitController.init(request);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("ACK", response.getBody().getMessage().getAck().getStatus());

        verify(schemaValidator).validateRequest(request);
        verify(schemaValidator).validateOrderContext(context);
        
        ArgumentCaptor<OndcTransaction> txnCaptor = ArgumentCaptor.forClass(OndcTransaction.class);
        verify(transactionRepository).save(txnCaptor.capture());
        
        OndcTransaction savedTxn = txnCaptor.getValue();
        assertEquals("txn-123", savedTxn.getTransactionId());
        assertEquals("msg-456", savedTxn.getMessageId());
        assertEquals("init", savedTxn.getAction());
        assertEquals("RECEIVED", savedTxn.getState());

        verify(callbackService).processInitAsync(request);
    }
}
