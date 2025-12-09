import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {

  name = "";
  email = "";
  phone = "";
  password = "";
  role = "CUSTOMER";

  errorMsg = "";
  successMsg = "";

  constructor(private auth: AuthService, private router: Router) { }

  onRegister() {
    const data = {
      name: this.name,
      email: this.email,
      phone: this.phone,
      password: this.password,
      role: this.role
    };

    console.log("Attempting registration with:", { ...data, password: '***' });

    this.auth.register(data).subscribe({
      next: (response) => {
        console.log("Registration successful:", response);
        this.successMsg = "Registration successful!";
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => {
        console.error("Registration error:", err);
        this.errorMsg = err.error?.message || "Registration failed. Email already exists.";
      }
    });
  }
}
