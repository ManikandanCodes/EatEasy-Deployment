import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/services/auth.service';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { FooterComponent } from './shared/components/footer/footer.component';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, FooterComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(private auth: AuthService) { }

  ngOnInit() {
    
    if (this.auth.isLoggedIn()) {
      this.auth.validateSession().subscribe({
        next: (user) => {
          console.log('Session validated:', user);
        },
        error: (err) => {
          console.warn('Session invalid, logging out:', err);
          this.auth.logout();
        }
      });
    }
  }
}
