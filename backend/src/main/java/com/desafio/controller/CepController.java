package com.desafio.controller;

import com.desafio.dto.CepDTO;
import com.desafio.service.CepService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cep")
@RequiredArgsConstructor
public class CepController {

    private final CepService cepService;

    @GetMapping("/{cep}")
    public ResponseEntity<CepDTO> consultar(@PathVariable String cep) {
        return ResponseEntity.ok(cepService.validarCep(cep));
    }
}
