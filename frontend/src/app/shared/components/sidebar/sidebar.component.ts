import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-mark">
          <svg width="20" height="20" fill="none" viewBox="0 0 24 24" stroke="white" stroke-width="2">
            <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z"/>
          </svg>
        </div>
        <div>
          <div class="brand-name">Fornece</div>
          <div class="brand-sub">GESTAO</div>
        </div>
      </div>

      <div class="nav-label">Menu</div>

      <button class="nav-item" routerLink="/empresas" routerLinkActive="active">
        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
        </svg>
        Empresas
      </button>

      <button class="nav-item" routerLink="/fornecedores" routerLinkActive="active">
        <svg fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z"/>
        </svg>
        Fornecedores
      </button>

      <div class="sidebar-foot">
        <div class="user-chip">
          <div class="avatar">FC</div>
          <div>
            <div class="user-name">Francisco</div>
            <div class="user-mail">admin</div>
          </div>
        </div>
      </div>
    </aside>
  `,
  styles: [`
    .sidebar {
      background: var(--surface);
      border-right: 1px solid var(--border);
      display: flex;
      flex-direction: column;
      padding: 18px 14px;
      gap: 4px;
      height: 100%;
    }
    .brand { display: flex; align-items: center; gap: 11px; padding: 6px 8px 18px; }
    .brand-mark {
      width: 38px; height: 38px; border-radius: 11px;
      background: linear-gradient(150deg, #6366f1, #4f46e5 60%, #4338ca);
      display: grid; place-items: center;
      box-shadow: 0 4px 12px rgba(79,70,229,.32);
    }
    .brand-name { font-family: var(--font-dp); font-weight: 700; font-size: 16px; letter-spacing: -.01em; }
    .brand-sub { font-size: 11.5px; color: var(--text-3); font-weight: 600; letter-spacing: .02em; }
    .nav-label {
      font-size: 10.5px; font-weight: 700; letter-spacing: .08em;
      text-transform: uppercase; color: var(--text-3); padding: 14px 10px 6px;
    }
    .nav-item {
      display: flex; align-items: center; gap: 11px;
      padding: 9px 10px; border-radius: var(--r-sm);
      color: var(--text-2); font-weight: 600; font-size: 14px;
      border: none; background: transparent; width: 100%; text-align: left;
      transition: background .14s, color .14s; cursor: pointer;
    }
    .nav-item svg { width: 18px; height: 18px; flex-shrink: 0; }
    .nav-item:hover { background: var(--bg-tint); color: var(--text); }
    .nav-item.active { background: var(--accent-soft); color: var(--accent-text); }
    .nav-item.active svg { color: var(--accent); }
    .sidebar-foot { margin-top: auto; padding-top: 14px; border-top: 1px solid var(--border); }
    .user-chip { display: flex; align-items: center; gap: 10px; padding: 7px 8px; border-radius: var(--r-sm); }
    .avatar {
      width: 34px; height: 34px; border-radius: 50%;
      display: grid; place-items: center;
      font-weight: 700; font-size: 13px; color: #fff;
      background: linear-gradient(140deg, #f59e0b, #ef4444);
    }
    .user-name { font-size: 13.5px; font-weight: 700; }
    .user-mail { font-size: 11.5px; color: var(--text-3); }
  `]
})
export class SidebarComponent {}
