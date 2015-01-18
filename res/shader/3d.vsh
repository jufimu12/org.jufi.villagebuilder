varying vec4 diffuse,ambient;
varying vec3 normal,halfVector;
varying vec4 position;

void main()
{
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
	
	normal = normalize(gl_NormalMatrix * gl_Normal);
	halfVector = gl_LightSource[0].halfVector.xyz;
	diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse;
	ambient = gl_FrontMaterial.ambient * (gl_LightSource[0].ambient + gl_LightModel.ambient);
	position = gl_Position;
	gl_TexCoord[0]  = gl_MultiTexCoord0;
}
