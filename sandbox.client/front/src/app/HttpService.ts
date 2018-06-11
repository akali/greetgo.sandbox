import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {Headers, Http, RequestOptionsArgs, Response} from "@angular/http";

class OptionsBuilder {
  private appendingHeaders: { [key: string]: string }[] = [];

  public appendHeader(key: string, value: string | null): void {
    if (value) this.appendingHeaders.push({key: key, value: value});
  }

  private get headers(): Headers {
    let ret = new Headers();
    this.appendingHeaders.forEach(h => ret.append(h['key'], h['value']));
    return ret;
  }

  public get(): RequestOptionsArgs | undefined {
    if (this.appendingHeaders.length == 0) return undefined;
    return {headers: this.headers};
  }
}

@Injectable()
export class HttpService {

  public pageSize:number = 10;

  private urlPrefix = "http://localhost:1414/access/api";

  constructor(private http: Http) {}

  private prefix(): string {
    return this.urlPrefix;
  }

  public get token(): string | null {
    return sessionStorage.getItem("token");
  }

  public set token(value: string | null) {
    if (value) sessionStorage.setItem("token", value);
    else sessionStorage.removeItem("token")
  }

  public url(urlSuffix: string): string {
    return this.prefix() + urlSuffix;
  }

  public get(urlSuffix: string, keyValue?: { [key: string]: string | number | null }): Observable<Response> {
    let post: string = '';

    if (keyValue) {

      let data = new URLSearchParams();
      let appended = false;
      for (let key in keyValue) {
        let value = keyValue[key];
        if (value) {
          data.append(key, value as string);
          appended = true;
        }
      }

      if (appended) post = '?' + data.toString();
    }

    return this.http.get(this.url(urlSuffix) + post, this.newOptionsBuilder().get());
  }

  private newOptionsBuilder(): OptionsBuilder {
    let ob = new OptionsBuilder();
    if (this.token) ob.appendHeader('Token', this.token);
    return ob;
  }

  public post(urlSuffix: string,
              keyValue: { [key: string]: string | number | boolean | null }): Observable<Response> {
    let data = new URLSearchParams();
    for (let key in keyValue) {
      let value = keyValue[key];
      if (value) data.append(key, value as string);
    }

    let ob = this.newOptionsBuilder();
    ob.appendHeader('Content-Type', 'application/x-www-form-urlencoded');

    return this.http.post(this.url(urlSuffix), data.toString(), ob.get());
  }

  public delete(urlSuffix: string,
                keyValue?: { [key: string]: string | number | boolean | null }): Observable<Response> {
    let post: string = '';

    if (keyValue) {

      let data = new URLSearchParams();
      let appended = false;
      for (let key in keyValue) {
        let value = keyValue[key];
        if (value) {
          data.append(key, value as string);
          appended = true;
        }
      }

      if (appended) post = '?' + data.toString();
    }

    return this.http.delete(this.url(urlSuffix) + post, this.newOptionsBuilder().get());
  }
}
