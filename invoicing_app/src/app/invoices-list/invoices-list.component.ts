import { Component, OnInit, Input } from '@angular/core';
import { TotalPerCustomer } from '../data/TotalPerCustomer';

@Component({
  selector: 'app-invoices-list',
  templateUrl: './invoices-list.component.html',
  styleUrls: ['./invoices-list.component.css'],  
})
export class InvoicesListComponent implements OnInit {


  @Input() total = Array<TotalPerCustomer>();

  constructor() { }

  ngOnInit(): void {
  }

}
