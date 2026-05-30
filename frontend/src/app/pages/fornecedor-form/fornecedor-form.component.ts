import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FornecedorService } from '../../services/fornecedor.service';
import { EmpresaService } from '../../services/empresa.service';
import { CepService } from '../../services/cep.service';
import { EmpresaResumo } from '../../models/empresa.model';
import { TipoPessoa } from '../../models/fornecedor.model';
import { applyMask, maskCpf, maskCnpj, maskCep, maskRg } from '../../utils/masks';
import { dataNaoFutura } from '../../utils/validators';

@Component({
  selector: 'app-fornecedor-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, MatSnackBarModule],
  templateUrl: './fornecedor-form.component.html',
  styleUrl: './fornecedor-form.component.scss'
})
export class FornecedorFormComponent implements OnInit {
  form!: FormGroup;
  isEditing = false;
  saving = false;
  fornecedorId?: number;
  cepValido = false;
  cepLoading = false;
  cepInfo = '';
  empresas: EmpresaResumo[] = [];
  empresaSearch = '';
  selectedEmpresaIds: Set<number> = new Set();
  hoje = new Date().toISOString().split('T')[0];

  get isPF(): boolean { return this.form?.get('tipoPessoa')?.value === 'FISICA'; }

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private fornecedorService: FornecedorService,
    private empresaService: EmpresaService,
    private cepService: CepService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      tipoPessoa: ['JURIDICA', Validators.required],
      nome: ['', Validators.required],
      cpfCnpj: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      cep: ['', [Validators.required, Validators.pattern(/^\d{5}-\d{3}$/)]],
      rg: [''],
      dataNascimento: ['']
    });

    this.form.get('tipoPessoa')!.valueChanges.subscribe(() => this.updatePFValidators());

    this.empresaService.listar().subscribe(data => this.empresas = data);

    const id = this.route.snapshot.params['id'];
    if (id) {
      this.isEditing = true;
      this.fornecedorId = +id;
      this.fornecedorService.buscarPorId(this.fornecedorId).subscribe(f => {
        this.form.patchValue(f);
        if (f.empresaIds) this.selectedEmpresaIds = new Set(f.empresaIds);
        this.updatePFValidators();
        this.consultarCep();
      });
    }
  }

  get empresasFiltradas(): EmpresaResumo[] {
    const term = this.empresaSearch.toLowerCase();
    return this.empresas.filter(e =>
      e.nomeFantasia.toLowerCase().includes(term) || e.cnpj.includes(term)
    );
  }

  isEmpresaSelecionada(id: number): boolean { return this.selectedEmpresaIds.has(id); }

  toggleEmpresa(id: number): void {
    if (this.selectedEmpresaIds.has(id)) this.selectedEmpresaIds.delete(id);
    else this.selectedEmpresaIds.add(id);
  }

  setTipoPessoa(tipo: TipoPessoa): void {
    this.form.get('tipoPessoa')!.setValue(tipo);
  }

  updatePFValidators(): void {
    const rg = this.form.get('rg')!;
    const dn = this.form.get('dataNascimento')!;
    if (this.isPF) {
      rg.setValidators([Validators.required, Validators.maxLength(12)]);
      dn.setValidators([Validators.required, dataNaoFutura]);
    } else {
      rg.clearValidators();
      dn.clearValidators();
      rg.setValue('');
      dn.setValue('');
    }
    rg.updateValueAndValidity();
    dn.updateValueAndValidity();
  }

  isInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!ctrl && ctrl.invalid && (ctrl.dirty || ctrl.touched);
  }

  maskCpfCnpj(event: Event): void {
    const masked = applyMask(event, this.isPF ? maskCpf : maskCnpj);
    this.form.get('cpfCnpj')!.setValue(masked, { emitEvent: false });
  }

  maskRg(event: Event): void {
    this.form.get('rg')!.setValue(applyMask(event, maskRg), { emitEvent: false });
  }

  maskCep(event: Event): void {
    this.form.get('cep')!.setValue(applyMask(event, maskCep), { emitEvent: false });
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
      },
      error: () => {
        this.cepLoading = false;
        this.snackBar.open('CEP nao encontrado', 'OK', { duration: 3000 });
      }
    });
  }

  salvar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    this.saving = true;
    const data = { ...this.form.value, empresaIds: Array.from(this.selectedEmpresaIds) };

    const obs = this.isEditing
      ? this.fornecedorService.atualizar(this.fornecedorId!, data)
      : this.fornecedorService.criar(data);

    obs.subscribe({
      next: () => {
        this.snackBar.open(
          this.isEditing ? 'Fornecedor atualizado com sucesso' : 'Fornecedor criado com sucesso',
          'OK', { duration: 3000 }
        );
        this.router.navigate(['/fornecedores']);
      },
      error: (err) => {
        this.saving = false;
        const msg = err.error?.error || 'Erro ao salvar fornecedor';
        this.snackBar.open(msg, 'OK', { duration: 5000 });
      }
    });
  }

  voltar(): void { this.router.navigate(['/fornecedores']); }
}
