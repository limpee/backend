package school.sptech.limpee.api.controller.documento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import school.sptech.limpee.api.repository.imagem.ImagemRepository;
import school.sptech.limpee.domain.documento.Documento;
import school.sptech.limpee.domain.documento.message.ResponseFile;
import school.sptech.limpee.domain.documento.message.ResponseMessage;
import school.sptech.limpee.domain.imagem.Imagem;
import school.sptech.limpee.service.documento.DocumentoService;
import school.sptech.limpee.service.documento.dto.DocumentoDto;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("documentos")
@CrossOrigin
public class DocumentoController {
    @Autowired
    private DocumentoService documentoService;

    @PostMapping(value = "/{idPrestador}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> uploadFile(@RequestBody MultipartFile file, @PathVariable long idPrestador) {
        String message = "";
        try {
            documentoService.store(file, idPrestador);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping
    public ResponseEntity<List<ResponseFile>> getListFiles() {
        List<ResponseFile> files = documentoService.getAllFiles().map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(dbFile.getId().toString())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getNome(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getArquivo().length);
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/download/{idPrestador}")
    public ResponseEntity<byte[]> getFile(@PathVariable long idPrestador) {
        DocumentoDto documento = documentoService.getFileByIdPrestador(idPrestador);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documento.getNome() + "\"")
                .body(documento.getArquivo());
    }
}
