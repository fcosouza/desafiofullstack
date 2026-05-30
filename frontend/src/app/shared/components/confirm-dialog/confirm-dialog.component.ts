import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [MatDialogModule],
  template: `
    <div class="modal-content">
      <div class="modal-ico" [style.background]="'var(--red-soft)'">
        <svg width="22" height="22" fill="none" viewBox="0 0 24 24" stroke="var(--red)" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round"
            d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4.5c-.77-.833-2.694-.833-3.464 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z"/>
        </svg>
      </div>
      <h3>{{ data.title }}</h3>
      <p>{{ data.message }}</p>
      <div class="modal-foot">
        <button class="btn btn-ghost" (click)="onCancel()">Cancelar</button>
        <button class="btn btn-danger" (click)="onConfirm()">Excluir</button>
      </div>
    </div>
  `,
  styles: [`
    .modal-content { padding: 4px; }
    .modal-ico {
      width: 46px; height: 46px; border-radius: 12px;
      display: grid; place-items: center; margin-bottom: 16px;
    }
    h3 { font-family: var(--font-dp); font-size: 18px; margin: 0 0 7px; }
    p { font-size: 14px; color: var(--text-2); margin: 0 0 22px; line-height: 1.5; }
    .modal-foot { display: flex; gap: 10px; justify-content: flex-end; }
  `]
})
export class ConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string }
  ) {}

  onCancel(): void { this.dialogRef.close(false); }
  onConfirm(): void { this.dialogRef.close(true); }
}
