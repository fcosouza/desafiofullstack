import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { FornecedorService } from '../../services/fornecedor.service';
import { FornecedorResumo } from '../../models/fornecedor.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-fornecedor-list',
  standalone: true,
  imports: [CommonModule, FormsModule, MatSnackBarModule, MatDialogModule],
  templateUrl: './fornecedor-list.component.html',
  styleUrl: './fornecedor-list.component.scss'
})
export class FornecedorListComponent implements OnInit {
  fornecedores: FornecedorResumo[] = [];
  fornecedoresFiltrados: FornecedorResumo[] = [];
  searchTerm = '';
  tipoFiltro = 'TODOS';

  get countPF(): number { return this.fornecedores.filter(f => f.tipoPessoa === 'FISICA').length; }
  get countPJ(): number { return this.fornecedores.filter(f => f.tipoPessoa === 'JURIDICA').length; }

  constructor(
    private fornecedorService: FornecedorService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.fornecedorService.listar().subscribe({
      next: (data) => { this.fornecedores = data; this.filtrar(); },
      error: () => this.snackBar.open('Erro ao carregar fornecedores', 'OK', { duration: 3000 })
    });
  }

  filtrar(): void {
    const term = this.searchTerm.toLowerCase();
    this.fornecedoresFiltrados = this.fornecedores.filter(f => {
      const matchTerm = f.nome.toLowerCase().includes(term) || f.cpfCnpj.includes(term);
      const matchTipo = this.tipoFiltro === 'TODOS' || f.tipoPessoa === this.tipoFiltro;
      return matchTerm && matchTipo;
    });
  }

  setTipoFiltro(tipo: string): void {
    this.tipoFiltro = tipo;
    this.filtrar();
  }

  novo(): void { this.router.navigate(['/fornecedores/novo']); }
  editar(id: number): void { this.router.navigate(['/fornecedores/editar', id]); }

  confirmarExclusao(f: FornecedorResumo): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: { title: 'Excluir fornecedor', message: `Tem certeza que deseja excluir "${f.nome}"? Esta acão nãoo pode ser desfeita.` }
    });

    ref.afterClosed().subscribe(result => {
      if (result) {
        this.fornecedorService.excluir(f.id).subscribe({
          next: () => { this.snackBar.open('Fornecedor excluido com sucesso', 'OK', { duration: 3000 }); this.carregar(); },
          error: () => this.snackBar.open('Erro ao excluir fornecedor', 'OK', { duration: 3000 })
        });
      }
    });
  }
}
