import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TotalPerCustomer } from '../data/TotalPerCustomer';
import { UploadData } from '../data/UploadData';
import { Observable, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BackendServiceService {
  baseUrl = 'http://localhost:8080/api';
  invoiceUrl = 'invoices'

  constructor(private http: HttpClient) { }


  getCustomers(data: UploadData): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();
    formData.append('file', data.file);
    formData.append('currencies', data.currencies);    
    formData.append('outputCurrency', data.outputCurrency);
    formData.append('filterByVat', (data.filterByVat as unknown as string));
    const req = new HttpRequest('POST', `${this.baseUrl}/${this.invoiceUrl}`, formData, {
      reportProgress: true,
      responseType: 'json'
    });
    return this.http.request(req);
  }
}
