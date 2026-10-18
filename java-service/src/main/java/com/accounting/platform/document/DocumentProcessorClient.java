package com.accounting.platform.document;

import com.accounting.platform.grpc.DocumentProcessorGrpc;
import com.accounting.platform.grpc.ProcessRequest;
import com.accounting.platform.grpc.ProcessResponse;
import io.grpc.ManagedChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentProcessorClient {

    private final ManagedChannel channel;

    public ProcessResponse processDocument(byte[] fileContent, String fileName, String hint, Long documentId) {
        DocumentProcessorGrpc.DocumentProcessorBlockingStub stub = 
                DocumentProcessorGrpc.newBlockingStub(channel);

        ProcessRequest request = ProcessRequest.newBuilder()
                .setDocumentId(documentId)
                .setFileName(fileName)
                .setFileContent(com.google.protobuf.ByteString.copyFrom(fileContent))
                .setDocumentTypeHint(hint != null ? hint : "")
                .build();

        log.info("Отправка файла '{}' в Python-сервис через gRPC", fileName);

        try {
            return stub.withDeadlineAfter(10, TimeUnit.SECONDS).processDocument(request);
        } catch (Exception e) {
            log.error("Ошибка gRPC-вызова к Python-сервису", e);
            throw new RuntimeException("Не удалось обработать документ в Python-сервисе", e);
        }
    }
}
