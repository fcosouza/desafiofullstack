import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  template: `
    <div class="app">
      <app-sidebar></app-sidebar>
      <main class="main">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .main {
      display: flex;
      flex-direction: column;
      overflow: hidden;
      min-width: 0;
      height: 100%;
    }
    :host {
      display: block;
      height: 100%;
    }
  `]
})
export class AppComponent {}
