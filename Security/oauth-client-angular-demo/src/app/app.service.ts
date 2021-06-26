import {Injectable} from '@angular/core';
import { Cookie } from 'ng2-cookies';
import { HttpClient, HttpHeaders } from '@angular/common/http';
//import { Observable } from 'rxjs/Observable';
//import 'rxjs/add/operator/catch';
//import 'rxjs/add/operator/map';
 
export class Foo {
  constructor(
    public id: number, 
    public name: string) 
  {}
}

export class Employee {
  constructor(
    public empno: number,
    public ename: string,
    public job: string
  )
  {}
}

/** 
  retrieveToken(): to obtain access token using authorization code
  saveToken(): to save our access token in a cookie using ng2-cookies library
  getResource(): to get a Foo object from server using its ID
  checkCredentials(): to check if user is logged in or not
  logout(): to delete access token cookie and log the user out
 */

@Injectable()
export class AppService {

  /**
   * USAMOS KEYCLOAK PARA GESTIONAR IDENTIDADES Y TOKENS DE ACCESO A SERVICIOS
   * EJECUTAMOS EL SERVIDOR KEYCLOAK (app SpringBoot)
   * Los realms allí representan servicios tipo Google, Facebook, etc. donde el usuario tiene una cuenta, a ellos se le piden datos del perfil del usuario (name, phone, mail, etc.)
   * Cada realm tiene sus clients. Estos serían las apps a las que le otorga permiso para utilizar su servicio. Digamos la app del cliente 'newClient' (definida abajo su clave)
   * Cada realm tiene cuentas de usuario. ie. Sería la persona que tiene la cuenta de Google.
   * Existe un realm Master para acceder al servidor.
   * Entonces:
   * Realm Google, tiene cliente X app con clientId='newClient' y una psw
   * User john@test.com / 123 tiene esa cuenta en Google, y va a acceder con su cuenta a la app de newClient.
   * 
   * CÓMO FUNCIONA ¿?
   * .siendo esta la url base del 'Authorization Server' 'http://localhost:8083/auth/realms/My App/protocol/openid-connect' (url)
   * 1. esta app llama a url/auth?client_id=..&redirect_uri=..
   * 2. se recibe un ?session_state=..&code=.. en la url
   * 3. tomamos code y llamamos a url/token?code=..&client_id=..&client_secret=..&redirect_uri=..
   * 4. guarda token en cookie (es un JWT)
   * 5. usa el token para hacer requests a la app a la que quiere acceder con token, el 'Resource Server'
   * 6. el 'Resource Server' recibe dicho token en la request y ahora lo valida con el 'Authorization Server' a ver si es auténtico
   * 7. responde adecuadamente!
   * 
   * ESTAS SON CREDENCIALES de esta app para poder usar el servicio del Authorization Server
   */ 
  public clientId = 'newClient';
  private clientSecret = 'newClientSecret';//'adfc14bb-b57b-4902-b694-af15ec5d3a34';

  public authServerBaseUri = 'http://localhost:8083/auth/realms/baeldung/protocol/openid-connect';
  public redirectUri = 'http://localhost:8089/';

  constructor(
    private _http: HttpClient)
  {}

    /**
     * In the retrieveToken method, we use our client credentials and Basic Auth send a POST to the “/openid-connect/token” endpoint to get the access token. The parameters are being sent in a URL encoded format. After we obtain the access token – we store it in a cookie
     * The cookie storage is especially important here, because we're only using the cookie for storage purposes and not to drive the authentication process directly. This helps protect against cross-site request forgery (CSRF) type of attacks and vulnerabilities.
     * 
     * el header 'Content-type': 'application/x-www-form-urlencoded; charset=utf-8'
     *  indica que me lo retorne por la url, a dif de application/json por ejemplo que será en el body
     */
  retrieveToken(code){
    let params = new URLSearchParams();   
    params.append('grant_type','authorization_code');
    params.append('client_id', this.clientId);
    params.append('client_secret', this.clientSecret);
    params.append('redirect_uri', this.redirectUri);
    params.append('code',code);

    let headers = new HttpHeaders({'Content-type': 'application/x-www-form-urlencoded; charset=utf-8'});
     
    this._http.post(this.authServerBaseUri+'/token', params.toString(), { headers: headers })
      .subscribe(
        data => this.saveToken(data),
        err => alert('Invalid Credentials')
      );
  }

  saveToken(token){
    var expireDate = new Date().getTime() + (1000 * token.expires_in);
    Cookie.set("access_token", token.access_token, expireDate);
    console.log('Obtained Access token '+token.access_token+', expires: '+expireDate);
    window.location.href = 'http://localhost:8089';
  }

  getResource(resourceUrl) : any { //: Observable<any>{
    var headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8', 
      'Authorization': 'Bearer ' + Cookie.get('access_token') // OAuth con Token (JWT)
    });
    return this._http.get(resourceUrl, { headers: headers });
                   //.catch((error:any) => Observable.throw(error.json().error || 'Server error'));
  }

  checkCredentials(){
    return Cookie.check('access_token');
  } 

  /**
   * el problema del logout es el sig:
   * cuando esta app elimina el storage, lo puede hacer sólo de su dominio,
   * es decir, cookies, session y local de localhost:8089, no las del dominio del Auth Server.
   * Es por eso que sigue quedando abierta la sesión, pq debe cerrarse es la session de localhost:8083 (la del Auth Server)
   * Efectivamente mi cookie se borró, pero se hace la solicitud nuevamente al AuthSrv y este devuelve un token y funciona,
   * pero si borramos su session, sí pedirá que inicie sesión nuevamente, pero no tenemos acceso a las de otro dominio dif al que ejecuta esta app
   */
  logout() {
    Cookie.delete('access_token');
    sessionStorage.clear();
    localStorage.clear();
    window.location.reload();
  }
}