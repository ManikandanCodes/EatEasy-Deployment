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
  confirmPassword = "";
  role = "CUSTOMER";
  showPassword = false;
  showConfirmPassword = false;

  otp = "";
  step = 1; // 1 = details, 2 = otp

  errorMsg = "";
  successMsg = "";

  constructor(private auth: AuthService, private router: Router) { }

  onRegister() {
    if (this.password !== this.confirmPassword) {
      this.errorMsg = "Passwords do not match!";
      return;
    }

    const data = {
      name: this.name,
      email: this.email,
      phone: this.phone,
      password: this.password,
      role: this.role
    };

    console.log("Attempting registration with:", { ...data, password: '***' });
    this.errorMsg = "";
    this.successMsg = "";

    this.auth.register(data).subscribe({
      next: (response) => {
        console.log("Registration initiated:", response);
        this.successMsg = "OTP sent to your email. Please verify.";
        this.step = 2; // Move to OTP step
      },
      error: (err) => {
        console.error("Registration error:", err);
        this.errorMsg = err.error?.message || "Registration failed. Email already exists.";
      }
    });
  }

  onVerifyOtp() {
    this.errorMsg = "";
    this.successMsg = "";

    this.auth.verifyOtp(this.email, this.otp).subscribe({
      next: (res) => {
        this.successMsg = "Verification successful! Logging in...";
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: (err) => {
        console.error("OTP Error:", err);
        this.errorMsg = err.error?.error || "Invalid OTP";
      }
    });
  }

  onResendOtp() {
    this.errorMsg = "";
    this.successMsg = "";

    this.auth.resendOtp(this.email).subscribe({
      next: () => {
        this.successMsg = "OTP has been resent to your email.";
      },
      error: (err) => {
        this.errorMsg = err.error?.error || "Failed to resend OTP";
      }
    });
  }

  allowOnlyNumbers(event: any) {
    const input = event.target;
    let value = input.value;

    // Remove non-numeric characters
    value = value.replace(/[^0-9]/g, '');

    // Limit to 6 characters
    if (value.length > 6) {
      value = value.slice(0, 6);
    }

    this.otp = value;
    input.value = value;
  }
}
