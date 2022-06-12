import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { HttpResponse, HttpEventType } from '@angular/common/http';
import { BackendServiceService } from '../services/backend-service.service';
import { TotalPerCustomer } from '../data/TotalPerCustomer';

@Component({
  selector: 'app-load-form',
  templateUrl: './load-form.component.html',
  styleUrls: ['./load-form.component.css']
})
export class LoadFormComponent implements OnInit {

  progress = 0;
  message = '';
  uploading = false;
  done = false;
  error = '';
  total = Array<TotalPerCustomer>();

  form = new FormGroup({
    currencies: new FormControl('', Validators.required),
    outputCurrency: new FormControl('', Validators.required),
    filterByVat: new FormControl(),
    file: new FormControl(null)
  });

  constructor(private uploadService: BackendServiceService) {
  }

  ngOnInit() { }

  onNew() {
    this.done = false;
  }

  onCloseMessage() {
    this.message = '';
  }

  uploadFile(event: any) {
    const target = (event!.target as HTMLInputElement);
    if (target !== null) {
      // @ts-ignore: Object is possibly 'null'.
      const file = target?.files[0];
      this.form.patchValue({
        file: file,
      });
      this.form.get('file')?.updateValueAndValidity();
    }
  }

  submitForm() {
    if (!this.validate()){
      return;
    }

    const data = {
      file: this.form.get('file')?.value,
      currencies: this.form.get('currencies')?.value,
      outputCurrency: this.form.get('outputCurrency')?.value,
      filterByVat: this.form.get('filterByVat')?.value ?? 0
    };
    this.uploading = true;
    this.total = Array<TotalPerCustomer>();
    this.done = false;
    this.message = '';
    this.uploadService.getCustomers(data).subscribe({
      next: (event: any) => {
        if (event.type === HttpEventType.UploadProgress) {
          this.progress = Math.round(100 * event.loaded / event.total);
        } else if (event instanceof HttpResponse) {
          console.log(event.body);
          this.total = event.body;
          this.uploading = false;
          this.done = true;
        }
      },
      error: (err: any) => {
        console.log(err);
        this.progress = 0;
        this.uploading = false;
        if (err.error && err.error.message) {
          this.message = err.error.message;
        } else {
          this.message = 'Could not upload the file!';
        }
      }
    });
  }

  validate(): boolean {
    if (!this.form.get('currencies')?.value){
      this.message = "Currencies rates are required!"
      return false;
    }
    if (!this.form.get('outputCurrency')?.value){
      this.message = "Output currency is required!"
      return false;
    }

    return true;
  }
}
