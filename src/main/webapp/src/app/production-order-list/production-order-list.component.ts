import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ProductionOrderResource} from "../model";

const API = "/api";
const REL = "productionOrders";

@Component({
  selector: 'app-production-order-list',
  templateUrl: './production-order-list.component.html',
  styleUrls: ['./production-order-list.component.css']
})
export class ProductionOrderListComponent implements OnInit {

  root: any;
  productionOrders?: ProductionOrderResource[];

  constructor(private http: HttpClient) {
  }

  ngOnInit(): void {
    let subscription = this.http.get(API).subscribe(
      response => {
        this.root = response;
        this.reload();
      },
      error => alert(error)
    );
  }

  can(action: string, order: any): boolean {
    return !!order._links[action];
  }

  do(action: string, order: ProductionOrderResource): void {
    let body = {};
    if (action === 'rename') {
      const newName = prompt("Please enter the new name:");
      if (!newName) {
        return;
      }
      body = {
        newName: newName
      };
    } else if (action === 'accept') {
      const expectedCompletionDate = prompt("Expected completion date (yyyy-MM-dd):");
      if (!expectedCompletionDate) {
        return;
      }
      body = {
        expectedCompletionDate: expectedCompletionDate
      }
    }
    const url = order._links[action].href;
    let subscription = this.http.post(url, body).subscribe(
      _ => this.reload(),
      response => alert([response.error.error, response.message].join("\n")));
  }

  private reload(): void {
    if (this.root) {
      let subscription = this.http.get<any>(this.root._links[REL].href).subscribe(
        response => this.productionOrders = response._embedded[REL],
        error => alert(error)
      );
    }
  }
}
