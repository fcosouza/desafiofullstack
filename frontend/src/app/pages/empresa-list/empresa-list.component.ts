import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { EmpresaService } from '../../services/empresa.service';
import { EmpresaResumo } from '../../models/empresa.model';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-empresa-list',
  standalone: true,
  imports: [CommonModule, FormsModule, MatSnackBarModule, MatDialogModule],
  templateUrl: './empresa-list.component.html',
  styleUrl: './empresa-list.component.scss'
})
export class EmpresaListComponent implements OnInit {
  empresas: EmpresaResumo[] = [];
  empresasFiltradas: EmpresaResumo[] = [];
  searchTerm = '';

  constructor(
    private empresaService: EmpresaService,
    private router: Router,
    private snackBar: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.carregar();
  }

  carregar(): void {
    this.empresaService.listar().subscribe({
      next: (data) => {
        this.empresas = data;
        this.filtrar();
      },
      error: () => this.snackBar.open('Erro ao carregar empresas', 'OK', { duration: 3000 })
    });
  }

  filtrar(): void {
    const term = this.searchTerm.toLowerCase();
    this.empresasFiltradas = this.empresas.filter(e =>
      e.nomeFantasia.toLowerCase().includes(term) ||
      e.cnpj.includes(term) ||
      e.cidade.toLowerCase().includes(term)
    );
  }

  nova(): void {
    this.router.navigate(['/empresas/novo']);
  }

  editar(id: number): void {
    this.router.navigate(['/empresas/editar', id]);
  }

  confirmarExclusao(empresa: EmpresaResumo): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      width: '420px',
      data: {
        title: 'Excluir empresa',
        message: `Tem certeza que deseja excluir "${empresa.nomeFantasia}"? Esta ação não pode ser desfeita.`
      }
    });

    ref.afterClosed().subscribe(result => {
      if (result) {
        this.empresaService.excluir(empresa.id).subscribe({
          next: () => {
            this.snackBar.open('Empresa excluida com sucesso', 'OK', { duration: 3000 });
            this.carregar();
          },
          error: () => this.snackBar.open('Erro ao excluir empresa', 'OK', { duration: 3000 })
        });
      }
    });
  }
}
