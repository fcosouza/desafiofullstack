import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { EmpresaService } from '../../services/empresa.service';
import { FornecedorService } from '../../services/fornecedor.service';
import { CepService } from '../../services/cep.service';
import { FornecedorResumo } from '../../models/fornecedor.model';

@Component({
  selector: 'app-empresa-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, MatSnackBarModule],
  templateUrl: './empresa-form.component.html',
  styleUrl: './empresa-form.component.scss'
})
export class EmpresaFormComponent implements OnInit {
  form!: FormGroup;
  isEditing = false;
  saving = false;
  empresaId?: number;
  cepValido = false;
  cepLoading = false;
  cepInfo = '';
  fornecedores: FornecedorResumo[] = [];
  fornecedorSearch = '';
  selectedFornecedorIds: Set<number> = new Set();

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private empresaService: EmpresaService,
    private fornecedorService: FornecedorService,
    private cepService: CepService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nomeFantasia: ['', Validators.required],
      cnpj: ['', [Validators.required, Validators.pattern(/^\d{2}\.\d{3}\.\d{3}\/\d{4}-\d{2}$/)]],
      cep: ['', [Validators.required, Validators.pattern(/^\d{5}-\d{3}$/)]],
      estado: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(2)]],
      cidade: ['', Validators.required]
    });

    this.fornecedorService.listar().subscribe(data => this.fornecedores = data);

    const id = this.route.snapshot.params['id'];
    if (id) {
      this.isEditing = true;
      this.empresaId = +id;
      this.empresaService.buscarPorId(this.empresaId).subscribe(empresa => {
        this.form.patchValue(empresa);
        if (empresa.fornecedorIds) {
          this.selectedFornecedorIds = new Set(empresa.fornecedorIds);
        }
        this.consultarCep();
      });
    }
  }

  get fornecedoresFiltrados(): FornecedorResumo[] {
    const term = this.fornecedorSearch.toLowerCase();
    return this.fornecedores.filter(f =>
      f.nome.toLowerCase().includes(term) || f.cpfCnpj.includes(term)
    );
  }

  isFornecedorSelecionado(id: number): boolean {
    return this.selectedFornecedorIds.has(id);
  }

  toggleFornecedor(id: number): void {
    if (this.selectedFornecedorIds.has(id)) {
      this.selectedFornecedorIds.delete(id);
    } else {
      this.selectedFornecedorIds.add(id);
    }
  }

  isInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!ctrl && ctrl.invalid && (ctrl.dirty || ctrl.touched);
  }

  maskCnpj(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '');
    if (v.length > 14) v = v.substring(0, 14);
    if (v.length > 12) v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{1,2})/, '$1.$2.$3/$4-$5');
    else if (v.length > 8) v = v.replace(/^(\d{2})(\d{3})(\d{3})(\d{1,4})/, '$1.$2.$3/$4');
    else if (v.length > 5) v = v.replace(/^(\d{2})(\d{3})(\d{1,3})/, '$1.$2.$3');
    else if (v.length > 2) v = v.replace(/^(\d{2})(\d{1,3})/, '$1.$2');
    this.form.get('cnpj')!.setValue(v, { emitEvent: false });
  }

  maskCep(event: Event): void {
    const input = event.target as HTMLInputElement;
    let v = input.value.replace(/\D/g, '');
    if (v.length > 8) v = v.substring(0, 8);
    if (v.length > 5) v = v.replace(/^(\d{5})(\d{1,3})/, '$1-$2');
    this.form.get('cep')!.setValue(v, { emitEvent: false });
  }

  consultarCep(): void {
    const cep = this.form.get('cep')?.value;
    if (!cep || cep.replace(/\D/g, '').length !== 8) return;

    this.cepLoading = true;
    this.cepValido = false;
    this.cepInfo = '';

    this.cepService.consultar(cep).subscribe({
      next: (resp) => {
        this.cepLoading = false;
        this.cepValido = true;
        this.cepInfo = `${resp.localidade} - ${resp.uf}`;
        this.form.patchValue({
          estado: resp.uf,
          cidade: resp.localidade
        });
      },
      error: () => {
        this.cepLoading = false;
        this.cepValido = false;
        this.snackBar.open('CEP não encontrado', 'OK', { duration: 3000 });
      }
    });
  }

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    const data = {
      ...this.form.value,
      estado: this.form.value.estado.toUpperCase(),
      fornecedorIds: Array.from(this.selectedFornecedorIds)
    };

    const obs = this.isEditing
      ? this.empresaService.atualizar(this.empresaId!, data)
      : this.empresaService.criar(data);

    obs.subscribe({
      next: () => {
        this.snackBar.open(
          this.isEditing ? 'Empresa atualizada com sucesso' : 'Empresa criada com sucesso',
          'OK', { duration: 3000 }
        );
        this.router.navigate(['/empresas']);
      },
      error: (err) => {
        this.saving = false;
        const msg = err.error?.error || 'Erro ao salvar empresa';
        this.snackBar.open(msg, 'OK', { duration: 5000 });
      }
    });
  }

  voltar(): void {
    this.router.navigate(['/empresas']);
  }
}
