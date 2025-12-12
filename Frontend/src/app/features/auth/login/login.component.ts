import { Component } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  email = '';
  password = '';
  confirmPassword = '';
  errorMsg = '';
  showPassword = false;
  showConfirmPassword = false;

  constructor(private auth: AuthService, private router: Router) { }

  Onlogin() {
    if (this.password !== this.confirmPassword) {
      this.errorMsg = 'Passwords do not match!';
      return;
    }

    const data = {
      email: this.email,
      password: this.password
    };

    this.auth.login(data).subscribe({
      next: (res: any) => {
        this.errorMsg = '';
        const role = res.role?.trim();

        if (role === 'ROLE_RESTAURANT_OWNER' || role === 'RESTAURANT_OWNER' || role === 'OWNER') {
          const isRegistered = res.isRestaurantRegistered || res.restaurantRegistered;
          if (isRegistered) {

            this.router.navigate(['/owner/dashboard']);
          } else {

            this.router.navigate(['/owner/add-restaurant']);
          }
        } else if (role === 'CUSTOMER') {
          this.router.navigate(['/customer/home']);
        } else if (role === 'ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else {
          this.errorMsg = `Unknown role: ${role}`;
        }
      },
      error: (err) => {
        console.error('Login error:', err);
        if (err.error && typeof err.error === 'string') {
          this.errorMsg = err.error;
        } else if (err.message) {
          this.errorMsg = err.message;
        } else {
          this.errorMsg = 'Invalid email or password!';
        }
      }
    });
  }

  skipLogin() {
    this.router.navigate(['/customer/home']);
  }
}
